<template>
  <div class="main">
    <div class="header">
      <MainHeader :text="wallet.name"/>
    </div>
    <div class ="list">
      <p> General information : </p>
      <p> Owner : {{ wallet.ownerName}}</p>
      <p> Address : {{ wallet.address }}</p>
      <p> Last consumptions :</p>
      <p v-if="!wallet.lastConsumptionOfWater">Water : No information</p>
      <p v-else>Water : {{ wallet.lastConsumptionOfWater }}</p>
      <p v-if="!wallet.lastConsumptionOfGas">Gas : No information</p>
      <p v-else>Gas : {{ wallet.lastConsumptionOfGas }}</p>
      <p v-if="!wallet.lastConsumptionOfElectricity">Electricity : No information</p>
      <p v-else>Electricity : {{ wallet.lastConsumptionOfElectricity }}</p>
      <p> Associated contracts :</p>
      <div v-if="wallet.contracts">
        <div v-for="contract in wallet.contracts" :key="contract.id">
          <p> name = {{ contract.name }}</p>
          <p> consumption = {{ contract.consumption }}</p>
          <p> price = {{ contract.price }}</p>
          <p>--------------------------</p>
          <div @click.prevent.left="seeMore(contract)">
            <GoButton text="button.go" :colore="'#34c98e'"/>
          </div>
        </div>
      </div>
      <div v-else> No information</div>
    </div>
    <div class="bottombutton">
      <div class="backbutton" @click.prevent.left="back()">
      <GoButton text="Back" :colore="'red'"/>
      </div>
      <div class="consumptionsbutton" @click.prevent.left="$router.push({name: 'Consumptions'})">
      <GoButton text="Consumptions" :colore="'#B1B9FC'"/>
      </div>
      <div class="closebutton" @click.prevent.left="deleteWallet()">
      <GoButton text="Close the wallet" :colore="'red'"/>
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
 /* /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }
  },
  /*Méthode qui récupère le wallet pour lequel on veut plus d'informations à la création de la vue*/
  async created(){
    const requestOptions = {
      method: "GET",
      headers: {'Authorization' : this.$cookies.get("token")}
    };
    try {
      const response = await fetch(`https://babawallet.alwaysdata.net/api/client/wallets/${this.address}`,requestOptions);
        if (!response.ok) { 
          const data = await response.text();
          if(response.status == 401 && data.trim() === ''){
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
          this.$cookies.remove("token");
          this.$cookies.remove("role");
          Swal.fire('Your connection has expired');
          this.$router.push("/");
        } 
        else {
          GlobalMethods.errorApi(error.message);
        }
    }
  },
  methods: {
    /* Méthode permettant de supprimer un portefeuille*/
    deleteWallet() {
      const requestOptions = {
        method: "DELETE",
        headers: {'Authorization' : this.$cookies.get("token")}
      };
      fetch(`https://babawallet.alwaysdata.net/api/client/wallets/${this.address}`, requestOptions)
          .then(response => {
            if(!response.ok){
              const data = response.text();
              if(response.status == 401 && data.trim() === ''){
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
                text: 'Wallet deleted !'
              })
              this.$router.push({name: 'Wallets'});
            }
          })
          .catch(error => {
            if(error.message === "Token") {
              this.$cookies.remove("token");
              this.$cookies.remove("role");
              Swal.fire('Your connection has expired');
              this.$router.push("/");
            } 
            else {
              GlobalMethods.errorApi(error.error);
            }
          });
    },
    /*Retourner à la page des wallets en supprimant l'adresse du sessionStorage*/
    back(){
      sessionStorage.removeItem('address');
      this.$router.push({name: 'Wallets'});
    },
    /*On sauvegarde le contrat sur lequel on souhaite plus d'informations
      et on redirige vers contrat*/
    seeMore(contract){
      sessionStorage.setItem('contract', contract);
      //this.$router.push( {name: "Contracts"} );
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
}

.list{
  display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 500px;
    height: 500px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}
</style>
