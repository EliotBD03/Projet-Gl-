<template>
    <div class="main">
      <div class="header">
      <MainHeader text="header.login"/>
      </div>
      <div class="login-form">
        <form id="loginForm" method="post" @submit.prevent="post">
          <InputMain :text="$t('account.mail')" v-model="mail" type="mail"/>
          <InputMain :text="$t('account.pwd')" v-model="pwd" type="password"/>
          <GoButton text="button.login" type="submit" :colore="'#34c98e'"/>
        </form>
        <div class="createbutton" @click.prevent.left="$router.push({name: 'createAccount'})">
        <GoButton text="button.createaccount" :colore="'#B1B9FC'"/>
        </div>
        <div class="forgotbutton" @click.prevent.left="goForgot()">
        <GoButton text="button.forgotpwd" :colore="'#B1B9FC'"/>
        </div>
        <!--<button v-on:click="test1()">TEST1 Client</button>
        <button v-on:click="test2()">TEST2 Supplier</button>-->
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
    name: "loginForm",
    components: {InputMain, GoButton, MainHeader},
    data(){
      return{
        mail: '',
        pwd: ''
      }},
    /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted() {
      if (this.$cookies.get("lang")) {
        this.$i18n.locale = this.$cookies.get("lang");
      } else {
        this.$cookies.set("lang", this.$i18n.locale)
      }
    },
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie une pop-up*/
        checkArgs(){
          if(!this.mail) Swal.fire(this.$t("alerts.mail"));
          else if(!this.pwd) Swal.fire(this.$t("alerts.pwd"));
          else return true;
        },
        /*Méthode qui envoie le mail et le mot de passe vers l'api si les champs sont remplis 
          quand l'utilisateur clique sur login.
          Si la requête est incorrecte, 
          l'api renvoie un message d'erreur et il y aura une pop-up explicative*/
          post(){
            if(this.checkArgs()) {
              const requestOptions = {
                method: "POST",
                body: JSON.stringify({ mail: this.mail, pwd: this.pwd })
              };
              fetch("https://babawallet.alwaysdata.net/log/check_account", requestOptions)
                .then(response => {
                  if(!response.ok){
                    return response.json().then(json => Promise.reject(json)); 
                    //crée une nouvelle promesse rejetée contenant l'objet JSON en tant qu'erreur
                  }
                  return response.json();
                }) 
                .then(data => {
                  this.$cookies.set("token", data.token);
                  this.$cookies.set("role", data.role);
                  GlobalMethods.isAClient();
                })
                .catch(error => {
                  GlobalMethods.errorApi(error.error);
                });
            }
          },
          /* Méthode permettant de vérifier si le champ "adresse mail" est bien rempli.
           Si c'est le cas, enregistre dans les cookies le mail et redirige vers la page
           ForgottenPassword.
           Sinon affichage d'une pop-up*/
        goForgot(){
          if(!this.mail) Swal.fire(this.$t("alerts.entermail"));
          else{
            this.$cookies.set("mail", this.mail);
            this.$router.push({name: 'forgottenPassword'});
          }
        },
        
        /*//Temp
        test1(){
          this.$cookies.set("role", "client");
          this.$cookies.set("token", 123456789); 
        },
        test2(){
          this.$cookies.set("role", "supplier");
          this.$cookies.set("token", 987654321);
          this.$router.push({ name: 'HomeSupplier' });
        }*/
        
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
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-form {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 500px;
    height: 500px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 20px 20px 60px #bebebe,
    -20px -20px 60px #ffffff;
  }
  </style>
