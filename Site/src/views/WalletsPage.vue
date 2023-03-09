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
          <div @click.prevent.left="seeMore(wallet)">
            <GoButton text="Go" :colore="'#34c98e'"/>
          </div>
         <div v-if="loading">Loading...</div>
        </div>
        <AddWalletForm/>
      </div>
    </div>
    <div class="homebutton" @click.prevent.left="$router.push('/Home')">
      <GoButton text="Home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import AddWalletForm from "@/components/AddWalletForm.vue";
import Swal from 'sweetalert2';
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
  components : {
    GoButton,
    MainHeader,
    AddWalletForm
  },
  data(){
    return{
      linkApi : "http://services-babawallet.alwaysdata.net:8300/api/client/wallets/",
      nbr : 1,
      loading : false,
      listWallet: []
    }},
  /*Au moment de la création on récupère déjà la première page de l'api*/
  created() {
    this.getPage();
  },
  methods: { //async -> requête qui peut prendre du temps, utile pour récupérer des données volumineuses
    async getPage(){
      const requestOptions = {
        method: "GET",
        headers: {'Authorization' : this.$cookies.get("token")},
      };
      this.loading = true; //bloquer les demandes de loader pendant ce temps.
      try {
        const response = await fetch("${linkApi}page?page=${nbr}", requestOptions);
        if (!response.ok) { //voir comment on gère l'arrivée à la fin des pages erreur/vide?
          if(response.status === 401){
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
          const data = await response.json(); //await-> attendre la fin du traitement pour continuer
          this.listWallet.push(data); //ajouter la suite de la réponse à la liste
        }
      } catch (error) {
        console.error(error);
      }
      this.loading = false;
    },
    loader()
    {
      if(!this.loading)
      {
        this.nbr++;
        this.getPage(this.nbr);
      }
    },
    /*On sauvegarde le wallet sur lequel on veut plus d'informations
    et on redirige vers walletFull*/
    seeMore(wallet){
      sessionStorage.setItem('address', wallet.address);
      this.$router.push({name: "WalletsFull"});
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