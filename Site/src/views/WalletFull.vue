<template>
  <div class="main">
    <div class="header">
      <MainHeader :text="wallet.name"/>
      <div class = "permission"> 
        <p v-if="permission == 'R'">{{ $t("GestionExtClaire.R") }}</p> 
        <p v-else-if="permission == 'RW'">{{ $t("GestionExtClaire.RW") }}</p> 
        <p v-else>{{ $t("GestionExtClaire.gestion") }}</p>
      </div>
    </div>
    <div class = "container">
      <div class ="list">
          <p class ="text"> <b>{{ $t("client.generalinformations") }} </b></p>
          <p> <b>{{ $t("wallet.owner") }} :</b> {{ wallet.ownerName}}</p>
        <p> <b>{{ $t("proposal.address") }} :</b> {{ wallet.address }}</p>
        <p> <b>{{ $t("wallet.numberOfResidents") }} :</b> {{ wallet.numberOfResidents}}</p>
        <p> <b>{{ $t("wallet.sizeOfHouse") }} (m²) :</b> {{ wallet.sizeOfHouse}}</p>
        <p> <b>{{ $t("wallet.typeOfHouse") }} :</b> {{ $t(typeOfHouse) }}</p>
        <p> <b>{{ $t("wallet.typeOfCharge") }} :</b> {{ $t(typeOfCharge) }}</p>
        <p> <b>{{ $t("wallet.solarPanels") }} :</b> {{ $t(solarPanels) }}</p>
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
            <div v-if="permission != 'R' && permission != 'RW'" @click.prevent.left="seeMore(contract)">
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

      <div class = "invited" v-if="permission == null">
        <p class = "text"> <b>{{ $t("GestionExtClaire.invited") }} </b></p>
        <div v-if="wallet.invitedClients && wallet.invitedClients.length !== 0">
          <div v-for="invited in wallet.invitedClients" :key="invited.id">
            <p> <b>{{ $t("GestionExtClaire.invitedName") }} :</b> {{ invited.invitedName }}</p>
            <p> <b>Mail :</b> {{ invited.invitedMail }}</p>
            <p> <b>Permission :</b> {{ invited.permission }}</p>
            <div @click.prevent.left="deleteClient(invited.invitedId)">
              <GoButton text="GestionExtClaire.deleteClient" :colore="'#34c98e'"/>
            </div>
            <div @click.prevent.left="modifyPerm(invited.invitedId)">
              <GoButton text="GestionExtClaire.permission" :colore="'#34c98e'"/>
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

      <div v-if="permission === 'R' || permission === 'RW'" class="closebutton" @click.prevent.left="deleteClient('no')">
      <GoButton text="GestionExtClaire.leave" :colore="'red'"/>
      </div>

      <div v-else>
        <div @click.prevent.left="$router.push({ name: 'AddInvited' })">
        <GoButton text="GestionExtClaire.addClient" :colore="'green'"/>
        </div>
        <div class="closebutton" @click.prevent.left="deleteWallet()">
        <GoButton text="button.closewallet" :colore="'red'"/>
        </div>
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
      permission : sessionStorage.getItem("permission"),
      wallet : [],
      typeOfHouse: "",
      typeOfCharge: "",
      solarPanels: ""
    }},
  /**
  * Cette méthode récupère le portefeuille pour lequel on veut plus d'informations à la création de la vue.
  * 
  * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
  */
  async created(){
    GlobalMethods.getCurrentLanguage();
    const requestOptions = {
      method: "GET",
      headers: {'Authorization' : this.$cookies.get("token")}
    };
    try {
      const response = await fetch(`https://babawallet.alwaysdata.net/api/client/wallets/${this.address}`,requestOptions);
        if (!response.ok) { 
          const data = await response.json();
          throw new Error(data.error);
        } 
        else {
          const data = await response.json();
          this.wallet = data.wallet;
          
          this.wallet.isHouse ? this.typeOfHouse = "walletform.typeHouse1" : this.typeOfHouse = "walletform.typeOfHouse2" ;
          this.wallet.isElectricityToCharge ? this.typeOfCharge = "walletform.typeCharge1" : this.typeOfCharge = "walletform.typeCharge2" ;
          this.wallet.solarPanels ? this.solarPanels = "walletform.solarPanel1" : this.solarPanels = "walletform.solarPanel2" ;
        }
    } catch(error) {
        if(error.message === "error.unauthorizedAccess")
            GlobalMethods.errorToken();
          else
            GlobalMethods.errorApi(error.message);
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
              return response.json().then(json => Promise.reject(json));
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
            if(error.error === "error.unauthorizedAccess")
              GlobalMethods.errorToken();
            else
              GlobalMethods.errorApi(error.error);
          });
    },
    /*Cette méthode permet de retourner à la page des wallets en supprimant l'adresse du sessionStorage*/
    /*Extension Claire : permet de retourner aussi à la page InvitedWallet*/
    back(){
      if(this.permission == "R" || this.permission == "RW"){
        sessionStorage.clear();
        this.$router.push({name: 'InvitedWallets'});
      }
      else{
        sessionStorage.clear();
        this.$router.push({name: 'Wallets'});
      }
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
    },
    /**
    * Cette méthode permet de supprimer un invité dans le cas où c'est le propriétaire du portefeuille qui le souhaite.
    * 
    * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
    * @author Extension Claire
    */
    deleteClient(id){
      const requestOptions = {
        method: "DELETE",
        headers: {'Authorization' : this.$cookies.get("token")}
      };
      fetch(`https://babawallet.alwaysdata.net/api/client/invitedClients/${this.address}/${id}`, requestOptions)
          .then(response => {
            if(!response.ok){
              return response.json().then(json => Promise.reject(json));
            }
            else{
              Swal.fire({
                icon: 'success',
                title: this.$t("alerts.good"),
                text: this.$t("GestionExtClaire.alertDelete")
              })
              this.$router.push({name: 'InvitedWallets'});
            }
          })
          .catch(error => {
            if(error.error === "error.unauthorizedAccess")
              GlobalMethods.errorToken();
            else
              GlobalMethods.errorApi(error.error);
          });
    },
    /**
    * Cette méthode de rediriger vers ChangePermissions en enregistrant l'id de l'invité dont on souhaite changer les permissions.
    * 
    * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
    * @author Extension Claire
    */
    modifyPerm(id){
      sessionStorage.setItem('invitedID', id);
      this.$router.push({name: 'ChangePermissions'});
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
  height: 110vh;
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
  height: 100vh;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  width: 33.33%;
  margin-right: 2%;
  margin-left: 3%;
}

.list > * {
  margin-bottom: 5px;
}

.contract {  
  width: 33.33%;
  height: 40%;
  overflow-y: scroll;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  margin-right: 3%;
}

.invited {  
  width: 33.3%;
  height: 40%;
  overflow-y: scroll;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  margin-right: 3%;
}


.text{
  color: rgb(138, 150, 253);
  font-size: 30px;
}

.permission{
  position: fixed;
  margin-top: 20px;
  margin-right: 20px;
  top: 0;
  right: 0;
  z-index: 9999;
  font-size: 25px;
}
</style>
