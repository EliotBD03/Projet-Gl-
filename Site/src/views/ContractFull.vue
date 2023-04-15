<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="contract.proposal.proposalName"/>
        </div>
        <div class ="list">
            <div class="content">
                <div v-if="isRole()">
                    <p> <b>{{ $t("proposal.associatedwallet") }} :</b> {{ associatedWallet }}</p>
                    <p> <b>{{ $t("proposal.address") }} :</b> {{ addressWallet }}</p>
                    <p> <b>{{ $t("client.eancode") }} :</b> {{ contract.ean }}</p>
                    <p> <b>{{ $t("account.provider") }} :</b> {{ contract.providerName }}</p>
                </div>
                <div v-else>
                    <p> <b>{{ $t("account.client") }}</b> {{ contract.clientName }}</p>
                    <p> <b>{{ $t("account.mail") }}</b> {{ mailClient }}</p>
                    <div @click.prevent.left="seeConsumptions(contract)">
                        <GoButton text="header.consumption" :colore="'#34c98e'"/>
                    </div>
                </div>
                <p><b>--------------------------</b></p>
                <p> <b>{{ $t("proposal.typeofenergy") }} :</b> {{ contract.proposal.typeOfEnergy.charAt(0).toUpperCase() + contract.proposal.typeOfEnergy.slice(1) }}</p>
                <p> <b>{{ $t("proposal.location") }} :</b> {{ convertLocation(contract.proposal.location) }}</p>
                <p> <b>{{ $t("proposal.priceperday") }} :</b> {{ contract.proposal.variableDayPrice }}€</p>
                <p> <b>{{ $t("proposal.pricepernight") }} :</b> {{ contract.proposal.variableNightPrice }}€</p>
                <p v-if="contract.proposal.startOfPeakHours"> <b>{{ $t("proposal.startofpeakhours") }} :</b> {{ contract.proposal.startOfPeakHours }}</p>
                <p v-if="contract.proposal.endOfPeakHours"> <b>{{ $t("proposal.endofpeakhours") }} :</b> {{ contract.proposal.endOfPeakHours }}</p>
                <p> <b>{{ $t("proposal.openingdate") }} :</b> {{ contract.openingDate }}</p>
                <p> <b>{{ $t("proposal.closingdate") }} :</b> {{ contract.closingDate }}</p>
                <p v-if="contract.proposal.fixedRate"><b>{{ $t("proposal.rate") }} : </b>{{ $t("proposal.fixed") }}</p>
                <p v-else><b>{{ $t("proposal.rate") }} : </b>{{ $t("proposal.variable") }}</p>
            </div>
        </div>
        <div class="bottombutton">
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'red'"/>
            </div>
            <div class="closebutton" @click.prevent.left="deleteContract()">
                <GoButton text="button.closecontract" :colore="'red'"/>
            </div>
        </div>
    </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import Swal from 'sweetalert2';
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
    components: {
        GoButton,
        MainHeader
    },
    data(){
        return{
            idContract : sessionStorage.getItem('idContract'),
            contract : [],
            associatedWallet: '',
            addressWallet: '',
            mailClient: ''
        }},
    created(){
        this.getContract();
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        async getContract() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")}
            };
            try {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/contracts/${this.idContract}`,requestOptions);
                if (!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                }
                else {
                    const data = await response.json();
                    this.contract = data.contract;
                }
            } catch(error) {
                if(error.error === "error.unauthorizedAccess")
                    GlobalMethods.errorToken();
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        /**
         * Cette méthode permet de supprimer un contrat.
         *
         * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
         */
        deleteContract() {
            const requestOptions = {
                method: "DELETE",
                headers: {'Authorization' : this.$cookies.get("token")}
            };
            fetch(`https://babawallet.alwaysdata.net/api/common/contracts/${this.idContract}`, requestOptions)
                .then(response => {
                    if(!response.ok){
                        return response.json().then(json => Promise.reject(json));
                    }
                    else{
                        Swal.fire({
                            icon: 'success',
                            title: this.$t("alerts.good"),
                            text: this.$t("alerts.deletecontract")
                        })
                        this.back();
                    }
                })
                .catch(error => {
                    if(error.error === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else {
                        GlobalMethods.errorApi(error.error);
                    }
                });
        },
        /*Cette méthode permet de retourner à la page client ou wallet en supprimant les informations du sessionStorage*/
        back(){
            sessionStorage.clear();
            if(this.isRole()){
                this.$router.push({name: 'Wallets'});
            }
            else{
                this.$router.push({name: 'Clients'});
            }
        },
        convertLocation: function(location) {
            const result = [];

            if (location >= 100) {
                result.push(this.$t("proposal.wallonia"));
                location -= 100;
            }

            if (location >= 10) {
                result.push(this.$t("proposal.flanders"));
                location -= 10;
            }

            if (location >= 1) {
                result.push(this.$t("proposal.brussels"));
            }

            return result.join(' - ');
        },
        /*Méthode permettant de gérer le v-if en fonction de s'il s'agit d'un client ou non*/
        isRole(){
            if(this.$cookies.get("role") == "client"){
                this.associatedWallet = sessionStorage.getItem('walletName');
                this.addressWallet = sessionStorage.getItem('walletAddress');
                return true;
            }
            this.mailClient = sessionStorage.getItem('clientMail');
            return false;
        },

        seeConsumptions(contract){
            sessionStorage.setItem('ean', contract.ean);
            sessionStorage.setItem('contractId', contract.contractId);
            this.$router.push({name: 'Consumptions'});
        }
    }
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 125vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.bottombutton {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 50px;
    width: 90%;
}

.list{
    display: flex;
    border-bottom: 1px solid gray;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    width: 550px;
    height: fit-content;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.content {
    margin-top: 10px;
    margin-bottom: 10px;
    display: flex;
    flex-direction: column;
}
</style>
  
