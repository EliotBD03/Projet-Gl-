<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.wallets"/>
    </div>
    <div class="allcards">
      <div v-infinite-scroll="loader">
        <div class=cards v-for="wallet in listWallet" :key="wallet.id">
          <div class="texte">
          <p> {{ wallet.name }} :</p>
          <p> {{ wallet.nameOwner }}</p>
          <p> {{ wallet.address }}</p>
          </div>
          <div @click.prevent.left="seeMore(wallet)">
            <GoButton text="button.go" :colore="'#34c98e'"/>
          </div>
         <div v-if="loading">{{ $t("wallets.loading") }}</div>
        </div>
        <AddWalletForm/>
      </div>
    </div>
    <div class="homebutton" @click.prevent.left="$router.push('/Home')">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
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
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }
  },
  data(){
    return{
      linkApi : "https://babawallet.alwaysdata.net/api/client/wallets/",
      nbr : 1,
      loading : false,
      listWallet: [
        { name: "Item 1", nameOwner: "BOb", address: "Rue ll", lastConsumptionOfWater: 10, lastConsumptionOfGas: 44, lastConsumptionOfElectricity: 90, listContracts: [{nom: "Engie", conso: "10000", prix : "400000"}, {nom: "paee", conso: "9000", prix : "5000"}]},

        ]
    }},
  /*Au moment de la création on récupère déjà la première page de l'api*/
  created() {
    this.getPage();
  },
  methods: {
    /*Méthode permettant de récupérer les pages des wallets de l'Api en scrollant */
    async getPage(){
      const requestOptions = {
        method: "GET",
        headers: {'Authorization' : this.$cookies.get("token")},
      };
      this.loading = true; //bloquer les demandes de loader pendant ce temps.
      try {
        const response = await fetch(`${this.linkApi}page?page=${this.nbr}`, requestOptions);
        if (!response.ok) { 
          const data = await response.text();
          if(response.status == 401 && data.trim() === ''){
            throw new Error("Token");
          }
          else{
            const data = await response.json();
            throw new Error(data.error);
          }
        } else {
          const data = await response.json(); 
          this.listWallet.push(data); //ajouter la suite de la réponse à la liste
        }
        this.loading = false;
      } catch(error) {
          if(error.message === "Token") {
            this.$cookies.remove("token");
            this.$cookies.remove("role");
            Swal.fire('Your connection has expired');
            this.$router.push("/");
          } 
          else {
            if(this.nbr == 1){
              console.log("hey")
              this.loading = true;
              Swal.fire('Aucun wallet à présenter');
            }
            else{
              GlobalMethods.errorApi(error.message);
            }
          }
      }
    },
    /*Lorsque l'utilisateur scrolle, cette méthode est appelée 
    pour augmenter le nombre de la page et appeler getPage*/
    loader()
    {
      if(!this.loading)
      {
        this.nbr++;
        this.getPage();
      }
    },
    /*On sauvegarde l'adresse du wallet sur lequel on souhaite plus d'informations
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
  align-items: center;
  height: 150vh;
}

.homebutton {
  display: flex;
  justify-content: center;
}

.allcards {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: space-evenly;
}

.cards {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  width: 400px;
  height: 500px;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  margin: 10px;
}

.texte {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 50px;
}
</style>