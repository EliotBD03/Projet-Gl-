<template>
  <div class="main">
    <div class="header">
      <MainHeader :text="wallet.name"/>
    </div>
    <div class = "container">
      <div class ="list">
          <p class ="text"> <b>{{ $t("client.generalinformations") }} </b></p>
          <p> <b>{{ $t("wallet.owner") }} :</b> {{ wallet.ownerName}}</p>
        <p> <b>{{ $t("proposal.address") }} :</b> {{ wallet.address }}</p>
        <p> <b>{{ $t("wallet.lastconsumptions") }} :</b></p>
        <p v-if="wallet.lastConsumptionOfWater"><b>{{ $t("proposal.water") }} :</b> {{ $t("client.noinformation") }}</p>
          <p v-else><b>{{ $t("proposal.water") }} :</b> {{ wallet.lastConsumptionOfWater }}</p>
        <p v-if="wallet.lastConsumptionOfGas"><b>{{ $t("proposal.gas") }} :</b> {{ $t("client.noinformation") }}</p>
        <p v-else><b>{{ $t("proposal.gas") }} :</b> {{ wallet.lastConsumptionOfGas }}</p>
        <p v-if="wallet.lastConsumptionOfElectricity"><b>{{ $t("proposal.electricity") }} :</b> {{ $t("client.noinformation") }}</p>
        <p v-else><b>{{ $t("proposal.electricity") }} :</b> {{ wallet.lastConsumptionOfElectricity }}</p>
      </div>
      <div class = "contract">
        <p class = "text"> <b>{{ $t("client.associatedcontracts") }} </b></p>
        <div v-if="wallet.contracts && wallet.contracts.length !== 0">
          <div v-for="contract in wallet.contracts" :key="contract.id">
            <p> <b>{{ $t("account.provider") }} :</b> {{ contract.providerName }}</p>
            <p> <b>{{ $t("client.eancode") }} :</b> {{ contract.ean }}</p>
            <div @click.prevent.left="seeMore(contract)">
              <GoButton text="button.go" :colore="'#34c98e'"/>
            </div>
            <div class="consumptionsbutton" @click.prevent.left="seeConsumptions(contract)">
              <GoButton text="header.consumption" :colore="'#B1B9FC'"/>
            </div>
            <p><b>--------------------------</b></p>
          </div>
        </div>
        <div v-else> <b>{{ $t("client.noinformation") }}</b></div>
      </div>
    </div>
    <div class="bottombutton">
      <div class="backbutton" @click.prevent.left="back()">
      <GoButton text="button.back" :colore="'red'"/>
      </div>
      <div class="closebutton" @click.prevent.left="deleteWallet()">
      <GoButton text="button.closewallet" :colore="'red'"/>
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
      address : sessionStorage.getItem('address'),
      wallet : []
    }},
  /**
  * Cette méthode récupère le portefeuille pour lequel on veut plus d'informations à la création de la vue.
  * 
  * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
  */
  async created(){
    const requestOptions = {
      method: "GET",
      headers: {'Authorization' : this.$cookies.get("token")}
    };
    try {
      const response = await fetch(`https://babawallet.alwaysdata.net/api/client/wallets/${this.address}`,requestOptions);
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
          this.wallet = data.wallet;
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
  methods: {
    /**
    * Cette méthode permet de supprimer un portefeuille.
    * 
    * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
    */
    deleteWallet() {
      const requestOptions = {
        method: "DELETE",
        headers: {'Authorization' : this.$cookies.get("token")}
      };
      fetch(`https://babawallet.alwaysdata.net/api/client/wallets/${this.address}`, requestOptions)
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
                title: this.$t("alerts.good"),
                text: this.$t("alerts.deletedwallet")
              })
              this.$router.push({name: 'Wallets'});
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
    /*Cette méthode permet de retourner à la page des wallets en supprimant l'adresse du sessionStorage*/
    back(){
      sessionStorage.clear();
      this.$router.push({name: 'Wallets'});
    },
    /*Méthode permettant de sauvegarder le contrat sur lequel on souhaite plus d'informations et rediriger vers contratFull*/
    seeMore(contract){
      sessionStorage.setItem('walletName', this.wallet.name);
      sessionStorage.setItem('walletAddress', this.wallet.address);
      sessionStorage.setItem('idContract', contract.contractId);
      this.$router.push( {name: "ContractFull"} );
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
  height: 100vh;
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

.container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  overflow: auto;
  margin: 0 auto;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  width: 25%;
  float: left;
  margin-left: 20%;
}

.contract {  
  width: 30%;
  float: right;
  height: 60%;
  overflow-y: scroll;
  margin-right: 20%;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.text{
  color: rgb(138, 150, 253);
  font-size: 30px;
}

</style>
