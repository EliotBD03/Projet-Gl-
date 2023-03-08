<template>
  <div class="main">
    <div class="header">
      <MainHeader text= {{ wallet.name }}/>
    </div>
    <div class ="list">
      <p> General information : </p>
      <p> Owner : {{ wallet.nameOwner }}</p>
      <p> Address : {{ wallet.address }}</p>
      <p> Last consumptions :</p>
      <p> Electricity : {{ wallet.lastConsumptionOfWater }}</p>
      <p> Gas : {{ wallet.lastConsumptionOfGas }}</p>
      <p> Water : {{ wallet.lastConsumptionOfElectricity }}</p>
      <p> Associated contracts :</p>
      <div v-for="contract in wallet.listContracts" :key="contract.id">
        <p> nom = {{ contract.nom }}</p>
        <p> conso = {{ contract.conso }}</p>
        <p> prix = {{ contract.prix }}</p>
        <p>--------------------------</p>
        <!-- A voir pour le bouton Go, il faut que contract.vue soit fait-->
      </div>
    </div>
    <div class="bottombutton">
      <div class="backbutton" @click.prevent.left="back()">
      <GoButton text="Back"/>
      </div>
      <div class="consumptionsbutton" @click.prevent.left="$router.push('homeC/consumptions')">
      <GoButton text="Consumptions"/>
      </div>
      <div class="closebutton" @click.prevent.left="deleteWallet()">
      <GoButton text="Close the wallet"/>
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
  /*On récupère le wallet sur lequel on veut plus d'informations*/
  data(){
    return{
      address : JSON.parse(sessionStorage.getItem('address')),
      wallet : ''
    }},
  async created(){
    const requestOptions = {
      method: "GET",
      headers: this.$cookies.get("token")
    };
    try {
      const response = await fetch("http://services-babawallet.alwaysdata.net:8300/api/client/wallets/:${address}",requestOptions);
      if (!response.ok) {
        if(response.status == 401){
          this.$cookies.remove("token");
          this.$cookies.remove("role");
          Swal.fire('Your connection has expired');
          this.$router.push("/");
        }
        else{
          GlobalMethods.errorApi(response.status);
          throw new Error(response.status);
        }
      } else {
        const data = await response.json();
        this.wallet = data.wallet;
      }
    } catch (error) {
      console.error(error);
    }
  },
  methods: {
    /* Méthode permettant de supprimer un portefeuille*/
    deleteWallet() {
      const requestOptions = {
        method: "DELETE",
        headers: this.$cookies.get("token")
      };
      fetch("http://services-babawallet.alwaysdata.net:8300/api/client/wallets/:${address}", requestOptions)
          .then(response => {
            if(!response.ok){
              if(response.status == 405){
                const data = response.json();
                GlobalMethods.errorApi(data.error);
                throw new Error(data.error);
              }
              if(response.status == 401){
                this.$cookies.remove("role");
                this.$cookies.remove("token");
                Swal.fire('Your connection has expired');
                this.$router.push("/");
              }
              else{
                GlobalMethods.errorApi(response.status);
                throw new Error(response.status);
              }
            }
            else{
              Swal.fire({
                icon: 'success',
                title: 'Good !',
                text: 'Wallet deleted !'
              })
              this.$router.push("/wallets");
            }
          })
          .catch(error => {
            console.error(error);
          });
    },
    /*Retourner à la page des wallets en supprimant l'adresse du sessionStorage*/
    back(){
      sessionStorage.removeItem('address');
      this.$router.push("/wallets");
    }
  }
};
</script>

<style scoped>

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
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
  margin-top : 10%;
  text-align: center;
}
</style>