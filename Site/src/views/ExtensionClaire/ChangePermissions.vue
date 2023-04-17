<template>
    <div class="main">
      <div class="header">
      <MainHeader text="GestionExtClaire.ChangePerm"/>
      </div>
      <div class="modify-form">
        <div class="line">
          <input type="radio" id="R" value="R" v-model="permission">
          <label for="R">{{ $t("GestionExtClaire.R") }}</label>
          <br>
          <input type="radio" id="RW" value="RW" v-model="permission">
          <label for="RW">{{ $t("GestionExtClaire.RW") }}</label>
          <br>
        </div>
        <div @click.prevent.left="post()">
          <GoButton text="GestionExtClaire.buttonChange" :colore="'green'"/>
        </div>
        <div @click.prevent.left="back()">
          <GoButton text="button.back" :colore="'red'"/>
        </div>
      </div>
    </div>
  </template>
  
    <script>
    /**
    * @author Extension Claire
    */
    import GoButton from "@/components/GoButton.vue";
    import MainHeader from "@/components/MainHeader.vue";
    import GlobalMethods from "@/components/GlobalMethods.vue";
    import Swal from 'sweetalert2';
    export default {
      components: {GoButton, MainHeader},
      data(){
        return{
          id: sessionStorage.getItem('invitedID'),
          permission: 'R',
          address : sessionStorage.getItem('address')
        }},
        created(){
          GlobalMethods.getCurrentLanguage();
        },
        methods: {
          /**Méthode qui, lorsque l'utilisateur 
          * clique sur change, envoie l'id, l'adresse et 
          * la permission vers l'api.
          * Si la requête est incorrecte, l'api renvoie un message d'erreur.
          * Si elle est correcte affiche une pop-up de succès 
          *   
          * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
          */
          post(){
            const requestOptions = {
              method: "PUT",
              headers: {'Authorization': this.$cookies.get("token")},
              body: JSON.stringify({ id_client_invited: this.id, address: this.address, permission: this.permission })
            };
            fetch("https://babawallet.alwaysdata.net/api/client/invitedClients/permission", requestOptions)
              .then(response => {
                if(!response.ok){
                    return response.json().then(json => Promise.reject(json));
                }
                else{
                  Swal.fire({
                  icon: 'success',
                  title: this.$t('alerts.good'),
                  text: this.$t('GestionExtClaire.alertChange'),
                })
              }
              }) 
              .catch(error => {
                if(error.error === "error.unauthorizedAccess")
                  GlobalMethods.errorToken();
                else
                    GlobalMethods.errorApi(error.error);
              });
          },
          /*Cette méthode permet de retourner à la page des walletFull en supprimant l'id de l'invité du sessionStorage*/
          back(){
            sessionStorage.removeItem('invitedID');
            this.$router.push({name: 'WalletFull'});
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
    height: 80vh;
  }
  
    .modify-form {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      width: 500px;
      height: 400px;
      border-radius: 50px;
      background: #e0e0e0;
      box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    }
  
    .line{
      display: flex;
      flex-wrap: nowrap;
      justify-content: center;
      align-items: center;
      margin: 5px;
    }

    </style>