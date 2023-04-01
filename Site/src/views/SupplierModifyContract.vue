<template>
    <div class="main">
        <div class="header">
            <MainHeader text="Modify contract"/>
        </div>
        <div class="contact-form">
            <form id="addWallet" method="post" v-on:submit.prevent="post">
                <p>
                    <InputMain :text="$t('walletform.name')" v-model="name_proposal"/>
                </p>
                <p>
                    <InputMain :text="'Type of energy'" v-model="type_of_energy"/>
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
                    <InputMain :text="'Basic price'" v-model="basic_price"/>
                </p>
                <p>
                    <InputMain :text="'Night price'" v-model="variable_night_price"/>
                </p>
                <p>
                    <InputMain :text="'Day price'" v-model="variable_day_price"/>
                </p>
                <p>
                    Off-peak hours :
                </p>
                <div>
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
                    <InputMain :text="'Duration (YYYY-MM-DD)'" v-model="duration"/>
                </p>
                <p>
                    <input type="radio" id="Bi-hourly" value="false" v-model="is_single_hour_counter">
                    <label for="Bi-hourly">Bi-hourly counter</label>
                    <input type="radio" id="Mono-hourly" value="true" v-model="is_single_hour_counter">
                    <label for="Mono-hourly">Mono-hourly counter</label>
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
            type_of_energy: this.contract.type_of_energy,
            wallonie: false,
            flandre: false,
            bruxelles: false,
            localization: this.contract.localization,
            basic_price: this.contract.basic_price,
            variable_night_price: this.contract.variable_night_price,
            variable_day_price: this.contract.variable_day_price,
            is_single_hour_counter: this.contract.is_single_hour_counter,
            is_fixed_rate: this.contract.is_fixed_rate,
            duration: this.contract.duration,
            hours: [], // tableau des heures disponibles
            start_off_peak_hours: this.contract.start_off_peak_hours, // heure de début sélectionnée
            end_off_peak_hours: this.contract.end_off_peak_hours // heure de fin sélectionnée
        }},
    mounted() {
        // génération du tableau des heures disponibles
        for (let i = 0; i < 24; i++) {
            const hour = i < 10 ? `0${i}` : `${i}`;
            this.hours.push(`${hour}:00:00`);
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
            headers: {'Authorisation' : this.$cookies.get('token')}
        };
        try {
            const response = await fetch(`https://babawallet.alwaysdata.net/api/provider/proposals/${this.name_proposal}`,requestOptions);
            if (response.ok) {
                const data = await response.text();
                if (response.status === 401 && data.trim() === ''){
                    throw new Error('Token');
                }
                else {
                    const data = await response.json();
                    throw new Error(data.error);
                }
            }
            else {
                const data = await response.json();
                this.contract = data.contract;
                this.convertLocalization();
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
        convertLocalization() {
            if (this.localization.charAt(0) === '1') {
                this.wallonie = true
            } else {
                this.wallonie = false
            }
            if (this.localization.charAt(1) === '1') {
                this.flandre = true
            } else {
                this.flandre = false
            }
            if (this.localization.charAt(2) === '1') {
                this.bruxelles = true
            } else {
                this.bruxelles = false
            }
        },
        checkArgs() {
            if (!this.name_proposal) Swal.fire("Please enter the name proposal");
            else if (!this.type_of_energy) Swal.fire("Please enter the type of energy");
            else if (this.localization === '000') Swal.fire("Please enter the localization");
            else if (!this.basic_price) Swal.fire("Please enter the basic price");
            else if (!this.variable_night_price) Swal.fire("Please enter the variable night price");
            else if (!this.variable_day_price) Swal.fire("Please enter the variable day price");
            else if (!this.is_single_hour_counter) Swal.fire("Please select the counter type");
            else if (!this.is_fixed_rate) Swal.fire("Please select the rate type");
            else if (!this.duration) Swal.fire("Please enter the duration");
            else if (!this.start_off_peak_hours) Swal.fire("Please select the start off peak hours");
            else if (!this.end_off_peak_hours) Swal.fire("Please select the end off peak hours");
            else return true;
        },
        post() {
            if (this.checkArgs()) {
                const requestOptions = {
                    method: "POST",
                    body: JSON.stringify({
                        name_proposal: this.name_proposal,
                        type_of_energy: this.type_of_energy,
                        localization: this.localization,
                        basic_price: this.basic_price,
                        variable_night_price: this.variable_night_price,
                        variable_day_price: this.variable_day_price,
                        is_single_hour_counter: this.is_single_hour_counter,
                        is_fixed_rate: this.is_fixed_rate,
                        duration: this.duration,
                        start_off_peak_hours: this.start_off_peak_hours,
                        end_off_peak_hours: this.end_off_peak_hours
                    }),
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
    height: 100vh;
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