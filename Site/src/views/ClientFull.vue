<template>
    <div class="main">
      <div class="header">
        <MainHeader :text="client.name"/>
      </div>
      <div class ="list">
        <p> General information : </p>
        <p> Mail : {{ client.mail }}</p>
        <p> Associated contracts :</p>
        <div v-if="!listContract">
          <div v-for="contract in listContract" :key="contract.id">
            <p> name = {{ contract.name }}</p>
            <p> ean = {{ contract.ean }}</p>
            <p> Type Of Energie = {{ contract.typeOfEnergie }}</p>
            <p>--------------------------</p>
            <div @click.prevent.left="seeMore(contract)">
              <GoButton text="button.go" :colore="'#34c98e'"/>
            </div>
          </div>
        </div>
        <div v-else> No information</div>
      </div>
      <div class="bottombutton">
        <div class="backbutton" @click.prevent.left="back()">
        <GoButton text="Back" :colore="'red'"/>
        </div>
        <div class="closebutton" @click.prevent.left="deleteClient()">
        <GoButton text="Delete client" :colore="'red'"/>
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
    data(){
      return{
        client : JSON.parse(sessionStorage.getItem('client')),
        linkApi : `https://babawallet.alwaysdata.net/api/provider/clients/`,
        nbr : 1,
        loading : false,
        lastPage : 0,
        listContract: []
      }},
   /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted() {
      if (this.$cookies.get("lang")) {
        this.$i18n.locale = this.$cookies.get("lang");
      } else {
        this.$cookies.set("lang", this.$i18n.locale)
      }
    },
    /*Au moment de la création de la page, on récupère déjà la première page des contrats du client*/
    created(){
      this.getPage();
    },
    methods: {
      /**
      * Cette méthode permet de récupérer les pages des contracts du client avec le bouton seeMore (+à la création de la page).
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
          const response = await fetch(`${this.client.clientId}/contrats/${this.linkApi}page?page=${this.nbr}&limit=3`, requestOptions);
          if (!response.ok) { 
            if(response.status == 401){
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
            }
            else if(this.lastPage >= this.nbr){
              this.listContract.push(data.contracts); //ajouter la suite de la réponse à la liste
              this.listContract = this.listContract.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
              this.loading = false;
              console.log(this.listContract)
            }
          }
        } catch(error) {
            if(error.message === "Token") {
              GlobalMethods.errorToken();
            } 
            else {  
              GlobalMethods.errorApi(error.message);
            }
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
      /**
      * Cette méthode permet de supprimer un client.
      * 
      * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
      */
      deleteClient() {
        const requestOptions = {
          method: "DELETE",
          headers: {'Authorization' : this.$cookies.get("token")}
        };
        fetch(`https://babawallet.alwaysdata.net/api/clients/clients_of_provider/${this.client.id_client}`, requestOptions)
          .then(response => {
            if(!response.ok){
              if(response.status == 401){
                  throw new Error("Token");
              }
              else{
                return response.json().then(json => Promise.reject(json));
              }
            }
            else{
              Swal.fire({
                icon: 'success',
                title: 'Good !',
                text: 'Client deleted !'
              })
              this.$router.push({name: 'Clients'});
            }
          })
          .catch(error => {
            if(error.message === "Token") {
              GlobalMethods.errorToken();
            } 
            else {
              GlobalMethods.errorApi(error.error);
            }
          });
      },
      /*Retourner à la page des clients en supprimant le client du sessionStorage*/
      back(){
        sessionStorage.clear();
        this.$router.push({name: 'Clients'});
      },
      /**
      * Cette méthode sauvegarde le contrat sur lequel on souhaite plus d'informations et redirige vers contratFull.
      * 
      * @param contract le contrat à sauvegarder.
      */
      seeMore(contract){
        sessionStorage.setItem('idContract', contract.contractId);
        sessionStorage.setItem('clientMail', this.client.mail);
        this.$router.push( {name: "ContractFull"} );
      }
    }
  };
  </script>
  
  <style scoped>
  
  .main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999; 
  }
  
  .bottombutton {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 50px;
    width: 100%;
  }
  
  .list{
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
  </style>
  
