<template>
    <div class="main">
      <div class="header">
      <MainHeader text="GestionExtClaire.addClient"/>
      </div>
      <div class="add-form">
        <div class="line">
          <input type="radio" id="R" value="R" v-model="permission">
          <label for="R">{{ $t("GestionExtClaire.R") }}</label>
          <br>
          <input type="radio" id="RW" value="RW" v-model="permission">
          <label for="RW">{{ $t("GestionExtClaire.RW") }}</label>
          <br>
        </div>
        <form id="addForm" method="post" v-on:submit.prevent="post">
          <InputMain v-model="id" type="id" :text="$t('GestionExtClaire.enterId')"/>
          <GoButton text="button.add" type="submit" :colore="'#34c98e'"/>
        </form>
        <div @click.prevent.left="$router.push({ name: 'WalletFull' })">
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
    import InputMain from "@/components/InputMain.vue";
    export default {
      name: "addForm",
      components: {InputMain, GoButton, MainHeader},
      data(){
        return{
          id: '',
          permission: 'R',
          address : sessionStorage.getItem('address')
        }},
        created(){
          GlobalMethods.getCurrentLanguage();
        },
        methods: {
          /*Méthode qui vérifie si le champ id est bien rempli sinon envoie un pop-up*/
          checkArgs(){
            if(!this.id) Swal.fire(this.$t("GestionExtClaire.alertId"));
            else return true;
          },
          /**Méthode qui, lorsque l'utilisateur 
          * clique sur add, envoie l'id, l'adresse et 
          * la permission vers l'api si checkArgs() est true.
          * Si la requête est incorrecte, l'api renvoie un status d'erreur ou un message d'erreur
          * Si elle est correcte affiche une pop-up de succès 
          *   
          * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
          */
          post(){
            if(this.checkArgs())
            {
              const requestOptions = {
                method: "POST",
                headers: {'Authorization': this.$cookies.get("token")},
                body: JSON.stringify({ id_client_invited: this.id, address: this.address, permission: this.permission })
              };
              fetch("https://babawallet.alwaysdata.net/api/client/invitedWallets/proposeInvitation", requestOptions)
                .then(response => {
                  if(!response.ok){
                      return response.json().then(json => Promise.reject(json));
                   }
                  else{
                    Swal.fire({
                    icon: 'success',
                    title: this.$t('alerts.good'),
                    text: this.$t('GestionExtClaire.request'),
                  })
                }
                }) 
                .catch(error => {
                  if(error.error === "error.unauthorizedAccess"){
                    GlobalMethods.errorToken();
                  }
                  else if(error.error === "error.id") { 
                    GlobalMethods.errorApi(this.$t("GestionExtClaire.alertBadId"));
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
    z-index: 9999; 
  }
  
  .main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 80vh;
  }
  
    .add-form {
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