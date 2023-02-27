<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Wallets"/>
    </div>
    <div class="allcards">
      <div v-infinite-scroll="loader">
        <div v-for="wallet in listWallet" :key="wallet.id">
            <p> {{ wallet.name }} :</p>
            <p> {{ wallet.nameOwner }}</p>
            <p> {{ wallet.address }}</p>   
            <GoButton text="Go" redirect= "/walletFull" v-on:click="seeMore(wallet)"/>
            <div v-if="loading">Loading...</div>
        </div> 
        <AddWalletForm/>
      </div>
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
    //Get //token = ? checkaccount faire .token -> regarder le token dans header.
    return{
      linkApi : "https://babawallet.alwaysdata.net:8300/api/client/wallets",
      nbr : 1,
      loading : false,
      listWallet: [],
      wallet : JSON.parse(sessionStorage.getItem('wallet'))
    }},
    created() { 
      this.getData(this.nbr);
    },
    methods: { //async -> requête qui peut prendre du temps, utile pour récupérer des données volumineuses
        //headers: { }, -> token
        async getData(nbr){
          this.loading = true; //bloquer les demandes de loader pendant ce temps.
          try {
            response = await fetch("${linkApi}page?page=${nbr}");
            if (response.ok) {
              data = await reponse.json(); //await-> attendre la fin du traitement pour continuer
            } else {
              throw new Error("Requête incorrecte");
            }
          } catch (error) {
            console.error(error);
          }
          if(data != null || data != undefined) //voir comment on gère l'arrivée à la fin des pages erreur/vide?
          {
            this.listWallet = this.listWallet.concat(data); //ajouter la suite de la réponse à la liste
          }
          this.loading = false;
        },
        loader()
        {
          if(!this.loading)
          {
            this.nbr++;
            this.getData(this.nbr);
          }
        },
        seeMore(wallet){
          this.wallet = sessionStorage.setItem('wallet', JSON.stringify(wallet));
          window.location.href = "../../html/client/walletFull.html";
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