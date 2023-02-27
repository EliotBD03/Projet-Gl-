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
        <div v-for="contract in wallet.listContracts">
          <p> nom = {{ contract.nom }}</p> 
          <p> conso = {{ contract.conso }}</p>
          <p> prix = {{ contract.prix }}</p>
          <p>--------------------------</p>
          <!-- A voir pour le bouton Go, il faut que contract.vue soit fait-->
        </div>  
    </div>
    <div class="bottombutton">
      <GoButton text="Back" redirect="/walletsPage" />
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
      wallet : JSON.parse(sessionStorage.getItem('wallet')),
      address : wallet.address
    }},
    methods: {
        deleteWallet() {
          const requestOptions = {
              method: "DELETE",
              //headers: { }, -> token
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/client/wallets/:${address}", requestOptions)
              .then(response => {
                if(response.ok){ //permet de vérifier si la requête est 200-OK
                  return response.json();}
                else {
                  throw new Error("Incorrect request");
                }
              }) 
              .catch(error => {
                console.error(error);
            });
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