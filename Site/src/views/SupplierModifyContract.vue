<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.modifyproposal"/>
        </div>
        <div class="contact-form">
            <div class="content">
                <form id="addWallet" method="post" v-on:submit.prevent="post">
                    <p v-if="!contract.fixedRate">
                        {{ $t("proposal.priceperday") }} :
                        <InputMain :text="$t('proposal.priceperday')" v-model="variable_day_price"/>
                    </p>
                    <p v-if="checkCounter()">
                        {{ $t("proposal.pricepernight") }} :
                        <InputMain :text="$t('proposal.pricepernight')" v-model="variable_night_price"/>
                    </p>
                    <div v-if="checkCounter()">
                        <p>
                            {{ $t("proposal.peakhours") }} :
                        </p>
                        <label for="start-time">{{ $t("proposal.starthour") }} : </label>
                        <select id="start-time" v-model="start_off_peak_hours">
                            <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                        </select>
                        <label for="end-time"> {{ $t("proposal.endhour") }} : </label>
                        <select id="end-time" v-model="end_off_peak_hours">
                            <option v-for="hour in hours" :key="hour">{{ hour }}</option>
                        </select>
                    </div>
                    <GoButton text="button.change" type="submit" :colore="'green'"/>
                </form>
            </div>
        </div>
        <div class="backbutton" @click.prevent.left="back()">
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
            name_proposal: sessionStorage.getItem('name_proposal'),
            contract: [],
            variable_night_price: '',
            variable_day_price: '',
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
                this.variable_night_price = this.contract.variableNightPrice;
                this.variable_day_price = this.contract.variableDayPrice;
                this.start_off_peak_hours = this.contract.startOfPeakHours;
                this.end_off_peak_hours = this.contract.endOfPeakHours;
            }
        }
        catch(error) {
            if(error.message === 'Token') {
                this.$cookies.remove('token');
                this.$cookies.remove('role');
                Swal.fire(this.$t("alerts.connectionexpired"));
                this.$router.push('/');
            }
            else {
                GlobalMethods.errorApi(error.message);
            }
        }
    },
    /*Méthode qui vérifie si les champs sont bien remplis sinon envoie une pop-up*/
    methods: {
        checkCounter() {
            return parseFloat(this.variable_night_price) !== 0;
        },
        checkArgs() {
            if (this.variable_night_price === '' && this.checkCounter()) Swal.fire(this.$t("alerts.enternightprice"));
            else if (!this.variable_day_price && !this.contract.fixedRate) Swal.fire(this.$t("alerts.enterdayprice"));
            else if (this.checkCounter() && !this.start_off_peak_hours) Swal.fire(this.$t("alerts.selectstarthour"));
            else if (this.checkCounter() && !this.end_off_peak_hours) Swal.fire(this.$t("alerts.selectendhour"));
            else return true;
        },
        post() {
            if (this.checkArgs()) {
                const requestOptions = {
                    method: "POST",
                    body: JSON.stringify({
                        name_proposal: this.name_proposal,
                        type_of_energy: this.contract.typeOfEnergy,
                        localization: this.contract.location,
                        variable_night_price: parseFloat(this.variable_night_price),
                        variable_day_price: parseFloat(this.variable_day_price),
                        is_fixed_rate: this.contract.fixedRate,
                        duration: this.contract.duration/720,
                        start_off_peak_hours: this.start_off_peak_hours,
                        end_off_peak_hours: this.end_off_peak_hours
                    }),
                    headers: {'Authorization' : this.$cookies.get("token")}
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
                            title: this.$t('alerts.good'),
                            text: this.$t("alerts.modifiedproposal"),
                        })
                    )
            }
        },
        back(){
            sessionStorage.removeItem('name_proposal');
            this.$router.push({name: 'ContractsSupplier'})
        },
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
    justify-content: space-evenly;
    flex-direction: column;
    width: 600px;
    height: fit-content;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.content {
    margin-top: 20px;
    margin-bottom: 20px;
}

</style>
