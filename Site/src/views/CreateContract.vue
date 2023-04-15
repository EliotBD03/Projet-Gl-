<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.addproposal"/>
        </div>
        <div class="contact-form">
            <form id="addWallet" method="post" v-on:submit.prevent="post">
                <p>
                    <InputMain :text="$t('proposal.entername')" v-model="name_proposal"/>
                </p>
                <p>
                    {{ $t("proposal.typeofenergy") }} :
                </p>
                <p>
                    <input type="radio" id="Water" value="water" v-model="type_of_energy">
                    <label for="Water">{{ $t("proposal.water") }}</label>
                    <input type="radio" id="Gas" value="gas" v-model="type_of_energy">
                    <label for="Gas">{{ $t("proposal.gas") }}</label>
                    <input type="radio" id="Electricity" value="electricity" v-model="type_of_energy">
                    <label for="Electricity">{{ $t("proposal.electricity") }}</label>
                </p>
                <p>
                    <label>
                        <input type="checkbox" v-model="wallonie">
                        {{ $t("proposal.wallonia") }}
                    </label>
                    <label>
                        <input type="checkbox" v-model="flandre">
                        {{ $t("proposal.flanders") }}
                    </label>
                    <label>
                        <input type="checkbox" v-model="bruxelles">
                        {{ $t("proposal.brussels") }}
                    </label>
                </p>
                <p>
                    <InputMain :text="$t('proposal.priceperday')" v-model="variable_day_price"/>
                </p>
                <p>
                    <InputMain :text="$t('proposal.pricepernight')" v-model="variable_night_price"/>
                </p>
                <p v-if="checkCounter()">
                    {{ $t("proposal.peakhours") }} :
                </p>
                <div v-if="checkCounter()">
                    <label for="start-time">{{ $t("proposal.starthour") }} : </label>
                    <select id="start-time" v-model="start_off_peak_hours">
                        <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                    </select>
                    <label for="end-time"> {{ $t("proposal.endhour") }} : </label>
                    <select id="end-time" v-model="end_off_peak_hours">
                        <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                    </select>
                </div>
                <p>
                    <InputMain :text="$t('proposal.duration')" v-model="duration"/>
                </p>
                <p>
                    {{ $t("proposal.rate") }} :</p>
                <p>
                    <input type="radio" id="Fixed" value="true" v-model="is_fixed_rate">
                    <label for="Fixed">{{ $t("proposal.fixed") }} {{ $t("proposal.rate") }}</label>
                    <input type="radio" id="Variable" value="false" v-model="is_fixed_rate">
                    <label for="Variable">{{ $t("proposal.variable") }} {{ $t("proposal.rate") }}</label>
                </p>
                <GoButton text="button.add" type="submit" :colore="'green'"/>
            </form>
        </div>
        <div class="backbutton" @click.prevent.left="$router.push({name: 'ContractsSupplier'})">
            <GoButton text="button.back" :colore="'darkblue'"/>
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
            name_proposal: '',
            type_of_energy: '',
            wallonie: false,
            flandre: false,
            bruxelles: false,
            localization: '000',
            variable_night_price: 0,
            variable_day_price: '',
            is_fixed_rate: '',
            duration: '',
            hours: [], // tableau des heures disponibles
            start_off_peak_hours: null, // heure de début sélectionnée
            end_off_peak_hours: null // heure de fin sélectionnée
        }},
    created() {
        GlobalMethods.getCurrentLanguage();
    },
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
        checkArgs() {
            if (!this.name_proposal) Swal.fire(this.$t("alerts.enternameproposal"));
            else if (!this.type_of_energy) Swal.fire(this.$t("alerts.selecttypeofenergy"));
            else if (this.localization === '000') Swal.fire(this.$t("alerts.selectlocation"));
            else if (this.variable_night_price === '') Swal.fire(this.$t("alerts.enternightprice"));
            else if (!this.variable_day_price) Swal.fire(this.$t("alerts.enterdayprice"));
            else if (!this.is_fixed_rate) Swal.fire(this.$t("alerts.selectratetype"));
            else if (this.checkDuration(this.duration)) Swal.fire(this.$t("alerts.enterduration"));
            else if (this.checkCounter() && !this.start_off_peak_hours) Swal.fire(this.$t("alerts.selectstarthour"));
            else if (this.checkCounter() && !this.end_off_peak_hours) Swal.fire(this.$t("alerts.selectendhour"));
            else return true;
        },
        convertToBoolean(value) {
            if (value === 'true') return true;
            else return false;
        },
        checkCounter() {
            return parseFloat(this.variable_night_price) !== 0;
        },
        isNum(value) {
            return /^\d+$/.test(value);
        },
        checkDuration(value) {
            if (value === 'baba') return false;
            else return !this.isNum(value);
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
                        duration: this.duration,
                        start_off_peak_hours: this.start_off_peak_hours,
                        end_off_peak_hours: this.end_off_peak_hours
                    }),
                    headers: {'Authorization' : this.$cookies.get("token")},
                };
                try {
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
                        Swal.fire({
                            icon: 'success',
                            title: this.$t('alerts.good'),
                            text: this.$t("alerts.addedcontract"),
                        })
                    )
                } catch (error) {
                    if(error.error === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else{
                        GlobalMethods.errorApi(error);
                    }
                }
            }
        }
    }
}
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    height: 100vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 10px;
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