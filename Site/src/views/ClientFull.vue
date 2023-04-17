<template>
    <div class="main">
      <div class="header">
        <MainHeader :text="client.name"/>
      </div>
      <div class="container">
        <div class ="list">
          <p class="text"> {{ $t("client.generalinformations") }} </p>
          <p> Mail : {{ client.mail }}</p>
        </div>
        <div class="contract">
          <p class="text"> {{ $t("client.associatedcontracts") }}</p>
          <div v-if="listContract && listContract.length !== 0">
            <div v-for="contract in listContract" :key="contract.id">
              <p> {{ $t("client.eancode") }} = {{ contract.ean }}</p>
              <div @click.prevent.left="seeMore(contract)">
                <GoButton text="button.go" :colore="'#34c98e'"/>
              </div>
              <div @click.prevent.left="seeConsumption(contract)">
                <GoButton text="button.seeConsumption" :colore="'#34c98e'"/>
              </div>
              <p>--------------------------</p>
            </div>
          </div>
          <div v-else> {{ $t("client.noinformation") }}</div>
        </div>
      </div>
      <div class="bottombutton">
        <div class="backbutton" @click.prevent.left="back()">
        <GoButton text="button.back" :colore="'red'"/>
        </div>
        <div class="closebutton" @click.prevent.left="deleteClient()">
        <GoButton text="button.deleteclient" :colore="'red'"/>
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
    /*Au moment de la création de la page, on récupère déjà la première page des contrats du client*/
    created(){
      this.getPage();
      GlobalMethods.getCurrentLanguage();
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
          const response = await fetch(`${this.linkApi}${this.client.clientId}/contrats/page?page=${this.nbr}&limit=3`, requestOptions);
          if (!response.ok) { 
            const data = await response.json();
            throw new Error(data.error);
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
        fetch(`https://babawallet.alwaysdata.net/api/client/clients_of_provider/${this.client.clientId}`, requestOptions)
          .then(response => {
            if(!response.ok){
              return response.json().then(json => Promise.reject(json));
            }
            else{
              Swal.fire({
                icon: 'success',
                title: this.$t("alerts.good"),
                text: this.$t("alerts.deletedclient")
              })
              this.$router.push({name: 'Clients'});
            }
          })
          .catch(error => {
            if(error.error === "error.unauthorizedAccess")
              GlobalMethods.errorToken();
            else
                GlobalMethods.errorApi(error.error);
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
      },

      /**
      * Cette méthode sauvegarde le contrat sur lequel on souhaite
      *
      * @param contract le contrat à sauvegarder
      */
      seeConsumption(contract){
        sessionStorage.setItem('contractId', contract.contractId);
        sessionStorage.setItem('ean', contract.ean);
        sessionStorage.setItem('permissions', "RW");
        this.$router.push( {name: "Consumptions"} );
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
    width: 90%;
}

.container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  overflow: auto;
  margin: 0 auto;
  height: 100vh;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  width: 25%;
  float: left;
  margin-left: 20%;
}

.list > * {
  margin-bottom: 5px;
}

.contract {  
  width: 30%;
  float: right;
  height: 40%;
  overflow-y: scroll;
  margin-right: 20%;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.text{
  color: rgb(138, 150, 253);
  font-size: 30px;
}
  </style>
  
