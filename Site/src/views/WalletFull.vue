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
        <div v-for="contract in listWallet[getIndex()].listContracts">
          <p> nom = {{ contract.nom }}</p> 
          <p> conso = {{ contract.conso }}</p>
          <p> prix = {{ contract.prix }}</p>
          <p>--------------------------</p>
          <!-- A voir pour le bouton Go, il faut que contract.js soit fait-->
        </div>  
    </div>
    <div class="bottombutton">
      <GoButton text="Back" redirect="walletsPage.html" />
      <GoButton text="Consumptions"/>
      <GoButton text="Close the wallet" v-on:click="deleteWallet()"/>
    </div>
  </div>

</template>
<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";

export default {
  components: {
    GoButton,
    MainHeader
  }, 
  data(){
    return{
      listWallet: [],
      wallet : JSON.parse(sessionStorage.getItem('wallet'))
    }},
    //Get //token = ? (dans le lien) checkaccount faire .token
    created() {
        fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets")
          .then(response => response.json())
          .then(data => (this.listWallet = data.total));
    },
    methods: {
        seeMore(wallet){
          this.wallet = sessionStorage.setItem('wallet', JSON.stringify(wallet));
          window.location.href = "../../html/client/walletFull.html";
        },
        getIndex(){
          let index = (item) => item.name == this.wallet.name;
          return this.listWallet.findIndex(index);
        },
        deleteWallet() {
          const requestOptions = {
              method: "DELETE",
              headers: { "Content-Type": "application/json" },
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets/:address", requestOptions)
              .then(response => response.json())
          
          //window.location.href = "../../html/client/wallet.html";
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