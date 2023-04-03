<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Add a client"/>
    </div>
    <div>
      <select class="select" v-model="choiceProposal">
        <option disabled value="">Please select your proposal</option>
        <option v-for="proposal in listOfProposal" :key="proposal.id">{{ proposal.proposalName }}</option>
      </select>
      <div class="selectedList" v-for="client in listClient" :key="client.id">
        <div class="item">
          <p> {{ client.name }} </p>
        </div>
        <div @click.prevent.left="post(client)">
          <GoButton text="ADD" :colore="'green'"/>
        </div>
      </div>
      <div v-if="notLastPage()" @click.prevent.left="loader()">
        <GoButton text="See more (new) clients" :colore="'#B1B9FC'"/>
      </div>
    </div>
    <div @click.prevent.left="$router.push('/clients')">
      <GoButton text="Back" :colore="'red'"/>
    </div>
  </div>
</template>
    
    <script>
    import GoButton from "@/components/GoButton.vue";
    import GlobalMethods from "@/components/GlobalMethods.vue";
    import Swal from 'sweetalert2';
    import MainHeader from "@/components/MainHeader.vue";
    export default {
      components: { MainHeader, GoButton },
      data(){
        return{
          linkApi : "https://babawallet.alwaysdata.net/api/provider/clients/",
          linkApiProposals : "https://babawallet.alwaysdata.net/api/provider/proposals/",
          nbr : 1,
          nbrProposals: 1,
          loading : false,
          lastPage : 0,
          lastpageProposals :0,
          listClient: [],
          listOfProposal : [],
          choiceProposal : ''
        }},
      /*Au moment de la création de la page, on récupère déjà la première page des (nouveaux) clients et la liste des propositions*/
      created(){
        this.getAllProposals(); 
        this.getPage();
      },
      methods: {
        /**
        * Cette méthode récupère toutes les propositions du fournisseur.
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée  dans GlobalMethods.
        */
        async getAllProposals(){
          const requestOptions = {
          method: "GET",
          headers: {'Authorization' : this.$cookies.get("token")},
          };
          try {
            const response = await fetch(`${this.linkApiProposals}page?page=${this.nbrProposals}&limit=6`, requestOptions);
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
              this.lastpageProposals = data.last_page;
              if(this.lastpageProposals == 0){
                  Swal.fire("You don't have any proposals");  
              }
              while(this.lastpageProposals >= this.nbrProposals){
                this.listOfProposal.push(data.allProposals); //ajouter la suite de la réponse à la liste
                this.listOfProposal = this.listOfProposal.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension            
                this.nbrProposals++;
                if(this.lastpageProposals >= this.nbrProposals){
                  this.getAllProposals();  
                } 
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
        /*Méthode qui vérifie si le fournisseur a bien choisi une proposition sinon envoie une pop-up*/
        checkArgs(){
          if(!this.choiceProposal) Swal.fire("Please choose your proposal");
          else return true;
        },
        /**
        * Cette méthode permet de récupérer les pages des (nouveaux) clients avec le bouton seeMore (+à la création de la page).
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée  dans GlobalMethods.
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
                  Swal.fire('There are no clients on the application');          
              }
              else if(this.lastPage >= this.nbr){
                this.listClient.push(data.allClients); //ajouter la suite de la réponse à la liste
                this.listClient = this.listClient.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
                this.loading = false;
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
        * Cette méthode envoie la demande de contract au client concerné si checkArgs() est true.
        * 
        * @param client le client à qui on doit envoyer la demande.
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
        post(client){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "POST",
              headers: {'Authorization': this.$cookies.get("token")},
              body: JSON.stringify({ name_proposal: this.choiceProposal, id_client: client.clientId })
            };
            fetch("https://babawallet.alwaysdata.net/api/provider/propose_contract", requestOptions)
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
                      text: 'A message has been sent to the client !'
                  })
                }
              })
              .catch(error => {
                if (error.message === "Token") {
                  GlobalMethods.errorToken();
                } 
                else {
                  GlobalMethods.errorApi(error.error);
                }
              });
            }
          }
      }
    }
    </script>
    
    <style scoped>
    .header {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 20vh;
      z-index: 9999; 
    }
  
    .main {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }

    .selectedList{
      height: 15vh;
      display: flex;
      justify-content: space-between; 
      align-items: center;
      border-bottom: 1px solid gray;
    }

    .item{
      font-size: 20px;
    }

    .select{
      font-size: 18px;
      margin-bottom: 5vh;
    }

    </style>
  