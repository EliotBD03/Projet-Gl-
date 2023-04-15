<template>
    <div class="main">
      <div class="header">
      <MainHeader text="header.forgotpwd"/>
      </div>
      <div class="forgot-form">
        <div class="totalTexte">
        <div class="headText">{{ $t("account.emailsent") }}</div>
        <div class="headText">{{ $t("account.instructions") }}</div>
        </div>
        <form id="forgotForm" method="post" v-on:submit.prevent="post">
            <InputMain :text="$t('account.mailcode')" type="text" v-model="code"/>
            <InputMain :text="$t('account.newpwd')" type="password" v-model="newPassword"/>
            <InputMain :text="$t('account.pwdconfirm')" type="password" v-model="repeatedPassword"/>
            <GoButton text="button.submit" type="submit" :colore="'#34c98e'"/>
          </form>
          <div @click.prevent.left="getCode()">
          <GoButton text="button.newcode" :colore="'gray'"/>
          </div>
          <div @click.prevent.left="back()">
          <GoButton text="button.back" :colore="'red'"/>
          </div>
      </div>
    </div>
  </template>

<script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  import GlobalMethods from "@/components/GlobalMethods.vue";
  import Swal from 'sweetalert2';
  import InputMain from "@/components/InputMain.vue";
  export default {
    components: {InputMain, GoButton,MainHeader},
    data(){
      return{
        code: '',
        newPassword: '',
        repeatedPassword: ''
      }},
      /*A la création de la page, récupère le mail dans les cookies et l'envoie vers l'api pour que l'utilisateur puisse avoir le code*/
      created(){
          this.getCode();
      },
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie une pop-up.
          Vérifie également si les mots de passe sont identiques*/
        checkArgs(){
          if(!this.code) Swal.fire(this.$t("alerts.entercode"));
          else if(!this.newPassword) Swal.fire(this.$t("alerts.pwd"));
          else if(!this.repeatedPassword) Swal.fire(this.$t("alerts.pwdconfirm"));
          else if(this.repeatedPassword !== this.newPassword) Swal.fire(this.$t("alerts.pwdmatch"));
          else return true;
        },
        /**Méthode qui envoie le code reçu par mail et le nouveau mot de passe vers l'api si checkArgs() 
        * est true quand l'utilisateur clique sur submit.
        * Si la requête est incorrecte, l'api renvoie un message d'erreur
        * Si elle est correcte affiche une pop-up de succès et redirige
        *
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
        post(){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "PUT",
              body: JSON.stringify({ code: this.code, new_pwd: this.newPassword, mail: this.$cookies.get("mail") })
            };
            fetch("https://babawallet.alwaysdata.net/log/renitialize_pwd", requestOptions)
              .then(response => {
                  if(!response.ok){
                    this.$cookies.remove('mail');
                    throw response.json();  
                  }
                  else{
                    this.$cookies.remove('mail');
                    Swal.fire({
                        icon: 'success',
                        title: this.$t("alerts.good"),
                        text: this.$t("alerts.pwdchanged"),
                      })
                    this.$router.push("/");
                  }
              }) 
              .catch(error => {
                error.then(data => {
                  GlobalMethods.errorApi(data.error);
                  console.error("Error", data.error);
                  this.$router.push("/");
                });
              });
          }
        },
        /*Méthode permettant d'obtenir un (nouveau) code pour valider le changement de mot de passe*/
        getCode(){
          GlobalMethods.sendCode();
        },
        /*Retourner à la page login en supprimant le mail des cookies*/
        back(){
          this.$cookies.remove('mail');
          this.$router.push("/");
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
  height: 100vh;
}

  .forgot-form {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 500px;
    height: 700px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  }

  .headText {
    font-size: 28px;
    font-weight: bold;
  }

  .totalTexte {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
  }
  
  </style>
