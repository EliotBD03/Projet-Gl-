<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Your clients"/>
    </div>
    <div class="allcards">
      <div class=cards v-for="client in listClient" :key="client.id">
        <div class="texte">
          <p class="name"> {{ client.name }} :</p>
          <p> {{ client.mail }}</p>
        </div>
        <div @click.prevent.left="seeMore(client)">
          <GoButton text="button.go" :colore="'#34c98e'"/>
        </div>
    </div>
    <div v-if="notLastPage()" @click.prevent.left="loader()">
      <GoButton text="See more clients" :colore="'#B1B9FC'"/>
    </div>
    </div>
    <div @click.prevent.left="$router.push('/addClient')">
      <GoButton text="Add a client" :colore="'#B1B9FC'"/>
    </div>
    <div class="homebutton" @click.prevent.left="$router.push({ name: 'HomeSupplier' })">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>
  <script>
  import MainHeader from "@/components/MainHeader.vue";
  import GoButton from "@/components/GoButton.vue";
  import Swal from 'sweetalert2';
  import GlobalMethods from "@/components/GlobalMethods.vue";
  export default {
    components : {
      GoButton,
      MainHeader,
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
        linkApi : "https://babawallet.alwaysdata.net/api/provider/clients/clients_of_provider/",
        nbr : 1,
        loading : false,
        lastPage : 0,
        listClient: []
      }},
    /*Au moment de la création on récupère déjà la première page de l'api*/
    created() {
      this.getPage();
    },
    methods: {
      /*Méthode permettant de récupérer les pages des clients de l'Api en scrollant */
      async getPage(){
        const requestOptions = {
          method: "GET",
          headers: {'Authorization' : this.$cookies.get("token")},
        };
        this.loading = true; //bloquer les demandes de loader pendant ce temps.
        try {
          const response = await fetch(`${this.linkApi}page?page=${this.nbr}&limit=3`, requestOptions);
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
            this.lastPage = data.last_page;
            if(this.lastPage == 0){
                this.loading = true;
                Swal.fire('No client');          
            }
            else if(this.lastPage >= this.nbr){
              this.listClient.push(data.allHisClients); //ajouter la suite de la réponse à la liste
              this.listClient = this.listClient.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
              this.loading = false;
            }
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
      /*Méthode permettant de vérifier si la dernière page n'a pas encore été chargée 
      et si on est pas en cours de chargement*/
      notLastPage(){
        if(this.lastPage == this.nbr || this.loading == true){
          return false;
        }
        return true;
      },
      /*On sauvegarde le client sur lequel on souhaite plus d'informations
      et on redirige vers clientFull*/
      seeMore(client){
        sessionStorage.setItem('client', client);
        this.$router.push( {name: "ClientFull"} );
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
    height: 400px;
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
  </style>
  
