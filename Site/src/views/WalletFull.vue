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
      <GoButton text="Back" v-on:click="back()" />
      <GoButton text="Consumptions"/>
      <GoButton text="Close the wallet" v-on:click="deleteWallet()"/>
    </div>
  </div>

</template>
<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import Swal from 'sweetalert2';
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
          const response = await fetch("https://babawallet.alwaysdata.net:8300/api/client/wallets/:${address}",requestOptions);
          if (!response.ok) {
            if(response.status == 401){
              this.$cookies.remove("token");
              Swal.fire('Your connection has expired');
              window.location.href = "/Login.vue";
            }
            else{
              this.errorApi(response.status);
              throw new Error(response.status);
            }
          } else {
            this.wallet = await response.json();
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
            fetch("https://babawallet.alwaysdata.net:8300/api/client/wallets/:${address}", requestOptions)
              .then(response => {
                if(!response.ok){ 
                  if(response.status == 405){
                    const data = response.json();
                    this.errorApi(data.error);
                    throw new Error(data.error);
                  }
                  if(response.status == 401){
                    this.$cookies.remove("token");
                    Swal.fire('Your connection has expired');
                    window.location.href = "/Login.vue";
                  }
                  else{
                    this.errorApi(response.status);
                    throw new Error(response.status);
                  }
                }
                else{
                  Swal.fire({
                      icon: 'success',
                      title: 'Good !',
                      text: 'Wallet deleted !'
                    })
                  window.location.href = "/WalletsPage.vue";
                }
              }) 
              .catch(error => {
                console.error(error);
            });
        },
        /*Retourner à la page des wallets en supprimant l'adresse du sessionStorage*/
        back(){
          sessionStorage.removeItem('address');
          window.location.href = "/WalletsPage.vue";
        },
        /*Affiche le message d'erreur venant de l'api dans une pop-up*/
        errorApi(error){
          Swal.fire({
            icon: 'error',
            title: 'OH NO !',
            text: error
          })
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