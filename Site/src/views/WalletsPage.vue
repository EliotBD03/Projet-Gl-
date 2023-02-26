<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Wallets"/>
    </div>
    <div class="allcards">
      <div v-for="wallet in listWallet" :key="wallet.id">
          <p> {{ wallet.name }} :</p>
          <p> {{ wallet.nameOwner }}</p>
          <p> {{ wallet.address }}</p>   
          <GoButton text="Go" redirect= "/walletFull" v-on:click="seeMore(wallet)"/>
      </div> 
      <AddWalletForm/>
    </div>
    <div class="homebutton">
      <GoButton text="Home" redirect="/"/>
    </div>
  </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import WalletsCard from "@/components/WalletsCard.vue"
import GoButton from "@/components/GoButton.vue";
import AddWalletForm from "@/components/AddWalletForm.vue";
export default {
  components : {
    GoButton,
    WalletsCard,
    MainHeader,
    AddWalletForm
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
        }
      }
  }
</script>

<style scoped>

.header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}

.homebutton {
  display: flex;
  justify-content: center;
}

.allcards {
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-evenly;
}
</style>