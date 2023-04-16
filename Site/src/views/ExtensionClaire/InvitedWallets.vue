<template>
    <div class="main">
      <div class="header">
        <MainHeader text= "GestionExtClaire.wallets"/>
      </div>
      <div class="allcards">
        <div class=cards v-for="wallet in listWallet" :key="wallet.id">
          <div class="texte">
            <p class="name"> {{ wallet.name }} </p>
            <p><b>{{ $t("proposal.address") }} :</b></p>
            <p> {{ wallet.address }}</p>
            <p> {{ wallet.ownerName }} </p>
            <p v-if="wallet.permission === 'R'">{{ $t("GestionExtClaire.R") }}</p>
            <p v-else>{{ $t("GestionExtClaire.RW") }}</p>
          </div>
          <div @click.prevent.left="seeMore(wallet)">
            <GoButton text="button.go" :colore="'#34c98e'"/>
          </div>
        </div>
        <div v-if="notLastPage()" @click.prevent.left="loader()">
          <GoButton text="button.seemore" :colore="'#B1B9FC'"/>
        </div>
      </div>
      <div class="homebutton" @click.prevent.left="$router.push({ name: 'HomeClient' })">
        <GoButton text="header.home" :colore="'#B1B9FC'"/>
      </div>
    </div>
  </template>
  <script>
  /**
  * @author Extension Claire
  */
  import MainHeader from "@/components/MainHeader.vue";
  import GoButton from "@/components/GoButton.vue";
  import Swal from 'sweetalert2';
  import GlobalMethods from "@/components/GlobalMethods.vue";
  export default {
    components : {
      GoButton,
      MainHeader,
    },
    data(){
      return{
        linkApi : "https://babawallet.alwaysdata.net/api/client/invitedWallets/",
        nbr : 1,
        loading : false,
        lastPage : 0,
        listWallet: []
      }},
    /*Au moment de la création on récupère déjà la première page de l'api*/
    created() {
      GlobalMethods.getCurrentLanguage();
      this.getPage();
    },
    methods: {
      /**
      * Cette méthode permet de récupérer les pages des portefeuilles où le client est invité de l'Api avec le bouton seeMore (+à la création de la page).
      * 
      * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
      */
      async getPage(){
        const requestOptions = {
          method: "GET",
          headers: {'Authorization' : this.$cookies.get("token")},
        };
        this.loading = true; //bloquer les demandes de loader pendant ce temps.
        try {
          const response = await fetch(`${this.linkApi}page?page=${this.nbr}&limit=3`, requestOptions);
          if (!response.ok) { 
            const data = await response.json();
            throw new Error(data.error);
          } else {
            const data = await response.json(); 
            this.lastPage = data.last_page;
            if(this.lastPage == 0){
                this.loading = true;
                Swal.fire(this.$t("alerts.nowallet"));
            }
            else if(this.lastPage >= this.nbr){
              this.listWallet.push(data.wallets); //ajouter la suite de la réponse à la liste
              this.listWallet = this.listWallet.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
              this.loading = false;
            }
          }
        } catch(error) {
          if(error.message === "error.unauthorizedAccess")
            GlobalMethods.errorToken();
          else
            GlobalMethods.errorApi(error.message);
        }
      },
      /*Lorsque l'utilisateur appuie sur SeeMore, cette méthode est appelée 
      pour augmenter le nombre de la page et appeler getPage*/
      loader()
      {
         if(!this.loading)
        {
          this.nbr++;
          this.getPage();
        }
      },
      /*Méthode permettant de vérifier si la dernière page n'a pas encore été chargée 
      ou si on est pas en cours de chargement*/
      notLastPage(){
        if(this.lastPage == this.nbr || this.loading == true){
          return false;
        }
        return true;
      },
      /*Méthode permettant de sauvegarder l'adresse du portefeuille sur lequel on souhaite plus d'informations
      et rediriger vers walletFull*/
      seeMore(wallet){
        sessionStorage.setItem('address', wallet.address);
        sessionStorage.setItem('permission', wallet.permission);
        this.$router.push( {name: "WalletFull"} );
      }
    }
  }
  </script>
  
  <style scoped>
  
  .header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999; 
  }
  
  .main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
  }
  
  .homebutton {
    display: flex;
    justify-content: center;
  }
  .allcards {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    max-width: 1000px;
    margin-top: 10vh;
  }
  
  .cards {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    width: 250px;
    height: 450px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    margin: 20px;
    border-radius: 30px;
  }
  
  
  .texte {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 50px;
  }
  
  .name {
    color: rgb(138, 150, 253);
    font-size: 30px;
   }
  
   .permission{
    position: fixed;
    margin-top: 50px;
    margin-right: 20px;
    top: 0;
    right: 0;
    z-index: 9999;
    font-size: 25px;
  }
  </style>
  