<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="contract.proposal.proposalName"/>
        </div>
        <div class ="list">
            <div v-if="isRole()">
                <p> <b>Associated wallet :</b> {{ associatedWallet }}</p>
                <p> <b>Address :</b> {{ addressWallet }}</p>
                <p> <b>Ean :</b> {{ contract.ean }}</p>
                <p> <b>Provider :</b> {{ contract.providerName }}</p>
            </div>
            <div v-else>
                <p> <b>Client :</b> {{ contract.clientName }}</p>
                <p> <b>Mail :</b> {{ mailClient }}</p>
                <div @click.prevent.left="seeConsumptions(contract)">
                    <GoButton text="Consumptions" :colore="'#34c98e'"/>
                </div>
            </div>
            <p><b>--------------------------</b></p>
            <p> <b>Type Of Energy :</b> {{ contract.proposal.typeOfEnergy.charAt(0).toUpperCase() + contract.proposal.typeOfEnergy.slice(1) }}</p>
            <p> <b>Location :</b> {{ convertLocation(contract.proposal.location) }}</p>
            <p> <b>Price er day :</b> {{ contract.proposal.variableDayPrice }}€</p>
            <p> <b>Price per night :</b> {{ contract.proposal.variableNightPrice }}€</p>
            <p> <b>Start of peak-hours :</b> {{ contract.proposal.startOfPeakHours }}</p>
            <p> <b>End of peak-hours :</b> {{ contract.proposal.endOfPeakHours }}</p>
            <p> <b>Opening Date :</b> {{ contract.openingDate }}</p>
            <p> <b>Closing Date :</b> {{ contract.closingDate }}</p>
            <p v-if="contract.isFixedRate"><b>Rate : </b>Fixed</p>
            <p v-else><b>Rate : </b>Variable</p>
        </div>
        <div class="bottombutton">
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="Back" :colore="'red'"/>
            </div>
            <div class="closebutton" @click.prevent.left="deleteContract()">
                <GoButton text="Close the contract" :colore="'red'"/>
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
                    if(response.status == 401){
                        throw new Error("Token");
                    }
                    else{
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                }
                else {
                    const data = await response.json();
                    this.contract = data.contract;
                }
            } catch(error) {
                if(error.message === "Token") {
                    GlobalMethods.errorToken();
                }
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
                        if(response.status == 401){
                            throw new Error("Token");
                        }
                        else{
                            return response.json().then(json => Promise.reject(json));
                        }
                    }
                    else{
                        Swal.fire({
                            icon: 'success',
                            title: 'Good !',
                            text: 'Contract deleted !'
                        })
                        this.back();
                    }
                })
                .catch(error => {
                    if(error.message === "Token") {
                        GlobalMethods.errorToken();
                    }
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
                result.push('Wallonie');
                location -= 100;
            }

            if (location >= 10) {
                result.push('Flandre');
                location -= 10;
            }

            if (location >= 1) {
                result.push('Bruxelles-Capitale');
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
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 15vh;
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
    height: 800px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}
</style>
  
