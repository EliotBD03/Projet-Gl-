<template>
    <div class="main">
        <div class="header">
            <MainHeader text="Modify contract"/>
        </div>
        <div class="contact-form">
            <form id="addWallet" method="post" v-on:submit.prevent="post">
                <p>
                    Type of energy :
                </p>
                <p>
                    <input type="radio" id="Water" value="water" v-model="type_of_energy">
                    <label for="Water">Water</label>
                    <input type="radio" id="Gas" value="gas" v-model="type_of_energy">
                    <label for="Gas">Gas</label>
                    <input type="radio" id="Electricity" value="electricity" v-model="type_of_energy">
                    <label for="Electricity">Electricity</label>
                </p>
                <p>
                    <label>
                        <input type="checkbox" v-model="wallonie">
                        Wallonie
                    </label>
                    <label>
                        <input type="checkbox" v-model="flandre">
                        Flandre
                    </label>
                    <label>
                        <input type="checkbox" v-model="bruxelles">
                        Bruxelles-Capitale
                    </label>
                </p>
                <p>
                    <InputMain :text="'Day price'" v-model="variable_day_price"/>
                </p>
                <p>
                    <InputMain :text="'Night price'" v-model="variable_night_price"/>
                </p>
                <p v-if="checkCounter()">
                    Off-peak hours :
                </p>
                <div v-if="checkCounter()">
                    <label for="start-time">Heure de début : </label>
                    <select id="start-time" v-model="start_off_peak_hours">
                        <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                    </select>
                    <label for="end-time"> Heure de fin : </label>
                    <select id="end-time" v-model="end_off_peak_hours">
                        <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                    </select>
                </div>
                <p>
                    <InputMain :text="'Duration (in months)'" v-model="duration"/>
                </p>
                <p>
                    <input type="radio" id="Fixed" value="true" v-model="is_fixed_rate">
                    <label for="Fixed">Fixed rate</label>
                    <input type="radio" id="Variable" value="false" v-model="is_fixed_rate">
                    <label for="Variable">Variable rate</label>
                </p>
                <GoButton text="Change" type="submit" :colore="'green'"/>
            </form>
        </div>
        <div class="backbutton" @click.prevent.left="back()">
            <GoButton text="Back" :colore="'darkblue'"/>
        </div>
    </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";

export default {
    components: {InputMain, GoButton, MainHeader},
    data(){
        return{
            name_proposal: sessionStorage.getItem('name_proposal'),
            contract: [],
            type_of_energy: '',
            wallonie: false,
            flandre: false,
            bruxelles: false,
            localization: '',
            variable_night_price: 0,
            variable_day_price: '',
            is_fixed_rate: '',
            duration: '',
            hours: [], // tableau des heures disponibles
            start_off_peak_hours: null, // heure de début sélectionnée
            end_off_peak_hours: null // heure de fin sélectionnée
        }},

    mounted() {
        // génération du tableau des heures disponibles
        for (let i = 0; i < 24; i++) {
            const hour = i < 10 ? `0${i}` : `${i}`;
            this.hours.push(`${hour}:00:00`);
        }
        if (this.$cookies.get("lang")) {
            this.$i18n.locale = this.$cookies.get("lang");
        } else {
            this.$cookies.set("lang", this.$i18n.locale)
        }
    },
    watch: {
        wallonie() {
            this.updateLocation()
        },
        flandre() {
            this.updateLocation()
        },
        bruxelles() {
            this.updateLocation()
        }
    },
    async created() {
        const requestOptions = {
            method: 'GET',
            headers: {'Authorization' : this.$cookies.get('token')}
        };
        try {
            console.log(this.name_proposal);
            const response = await fetch(`https://babawallet.alwaysdata.net/api/provider/proposals/${this.name_proposal}`,requestOptions);
            if (!response.ok) {
                if (response.status === 401){
                    throw new Error('Token');
                }
                else {
                    const data = await response.json();
                    throw new Error(data.error);
                }
            }
            else {
                const data = await response.json();
                this.contract = data.proposal;
                this.convertLocalization(this.contract.location);
                this.type_of_energy = this.contract.typeOfEnergy;
                this.variable_night_price = this.contract.variableNightPrice;
                this.variable_day_price = this.contract.variableDayPrice;
                this.is_fixed_rate = this.contract.fixedRate;
                this.duration = parseInt(this.contract.duration);
                console.log(this.is_fixed_rate)
            }
        }
        catch(error) {
            if(error.message === 'Token') {
                this.$cookies.remove('token');
                this.$cookies.remove('role');
                Swal.fire('Your connection has expired');
                this.$router.push('/');
            }
            else {
                GlobalMethods.errorApi(error.message);
            }
        }
    },
    /*Méthode qui vérifie si les champs sont bien remplis sinon envoie une pop-up*/
    methods: {
        updateLocation() {
            let location = ''
            if (this.wallonie) {
                location += '1'
            } else {
                location += '0'
            }
            if (this.flandre) {
                location += '1'
            } else {
                location += '0'
            }
            if (this.bruxelles) {
                location += '1'
            } else {
                location += '0'
            }
            this.localization = location
        },
        convertLocalization(location) {
            if (location.charAt(0) === '1') {
                this.wallonie = true
            } else {
                this.wallonie = false
            }
            if (location.charAt(1) === '1') {
                this.flandre = true
            } else {
                this.flandre = false
            }
            if (location.charAt(2) === '1') {
                this.bruxelles = true
            } else {
                this.bruxelles = false
            }
        },
        checkCounter() {
            return parseFloat(this.variable_night_price) !== 0;
        },
        checkArgs() {
            if (!this.name_proposal) Swal.fire("Please enter the name proposal");
            else if (this.localization === '000') Swal.fire("Please enter the localization");
            else if (this.variable_night_price === '') Swal.fire("Please enter the variable night price");
            else if (!this.variable_day_price) Swal.fire("Please enter the variable day price");
            else if (!this.duration) Swal.fire("Please enter the duration");
            else if (this.checkCounter() && !this.start_off_peak_hours) Swal.fire("Please select the start off peak hours");
            else if (this.checkCounter() && !this.end_off_peak_hours) Swal.fire("Please select the end off peak hours");
            else return true;
        },
        convertToBoolean(value) {
            if (value === 'true') return true;
            else return false;
        },
        post() {
            if (this.checkArgs()) {
                const requestOptions = {
                    method: "POST",
                    body: JSON.stringify({
                        name_proposal: this.name_proposal,
                        type_of_energy: this.type_of_energy,
                        localization: this.localization,
                        variable_night_price: parseFloat(this.variable_night_price),
                        variable_day_price: parseFloat(this.variable_day_price),
                        is_fixed_rate: this.convertToBoolean(this.is_fixed_rate),
                        duration: parseInt(this.duration),
                        start_off_peak_hours: this.start_off_peak_hours,
                        end_off_peak_hours: this.end_off_peak_hours
                    }),
                    headers: {'Authorization' : this.$cookies.get("token")},
                };
                fetch("https://babawallet.alwaysdata.net/api/provider/proposals", requestOptions)
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(json => Promise.reject(json));
                        }
                        return response.json();
                    })
                    .then(
                        this.$router.push({name: 'HomeSupplier'}))
                    .then(
                        sessionStorage.removeItem('name_proposal')
                    )
                    .then(
                        Swal.fire({
                            icon: 'success',
                            title: this.$t('alert.good'),
                            text: 'Your contract has been modified',
                        })
                    )
            }
        },
        back(){
            sessionStorage.removeItem('name_proposal');
            this.$router.push({name: 'ContractsSupplier'})
        }
    }
}
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 120vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    padding: 10px;
}

.contact-form{
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 600px;
    height: 700px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

</style>