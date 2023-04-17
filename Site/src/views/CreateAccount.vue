<template>
  <div class="main">
    <div class="header">
    <MainHeader text="header.main"/>
    </div>
    <div class="create-form">
      <div class="texte">{{ $t("account.clickcode") }}</div>
      <div class="line">
        <input type="radio" id="Client" value="Client" v-model="role">
        <label for="Client">{{ $t("account.client") }}</label>
        <br>
        <input type="radio" id="Supplier" value="Supplier" v-model="role">
        <label for="Supplier">{{ $t("account.provider") }}</label>
        <br>
      </div>
      <div class="line">
        <input type="radio" id="français" value="fr" v-model="language">
        <label for="français">{{ $t("account.french") }}</label>
        <br>
        <input type="radio" id="english" value="en" v-model="language">
        <label for="english">{{ $t("account.english") }}</label>
        <br>
      </div>
      <form id="createForm" method="post" v-on:submit.prevent="post">
        <InputMain v-model="name" type="name" :text="$t('account.name')"/>
        <InputMain :text="$t('account.mail')" type="mail" v-model="mail"/>
        <InputMain :text="$t('account.pwd')" type="password" v-model="pwd"/>
        <InputMain :text="$t('account.pwdconfirm')" type="password" v-model="repeatedpwd"/>
        <InputMain :text="$t('account.code')" type="text" v-model="code"/>
        <GoButton text="button.createaccount" type="submit" :colore="'#34c98e'"/>
      </form>
      <div @click.prevent.left="getCode()">
        <GoButton text="button.sendcode" :colore="'#B1B9FC'"/>
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
    name: "createForm",
    components: {InputMain, GoButton, MainHeader},
    data(){
      return{
        name: '',
        mail: '',
        pwd: '',
        repeatedpwd: '',
        code: '',
        language: this.$i18n.locale,
        selectedList:[],
        role: 'Client',
        is_client: false
      }},
      watch: {
        language() {
          this.$i18n.locale = this.language;
        }
      },
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up*/
        checkArgs(){
          if(!this.name) Swal.fire(this.$t("alerts.name"));
          else if(!this.mail) Swal.fire(this.$t("alerts.mail"));
          else if(!this.pwd) Swal.fire(this.$t("alerts.pwd"));
          else if(!this.code) Swal.fire(this.$t("alerts.entercode"));
          else if(!this.repeatedpwd) Swal.fire(this.$t("alerts.pwdconfirm"));
          else if(this.repeatedpwd !== this.pwd) Swal.fire(this.$t("alerts.pwdmatch"));
          else return true;
        },
        /**Méthode qui, lorsque l'utilisateur 
        * clique sur create an account, envoie le nom de l'utilisateur, 
        * le mail, le code reçu par mail, 
        * le mot de passe choisi, la langue choisie
        * et s'il s'agit d'un client vers l'api si checkArgs() est true.
        * Si la requête est incorrecte, l'api renvoie un message d'erreur
        * Si elle est correcte affiche une pop-up de succès et redirige 
        *   
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
        post(){
          if(this.checkArgs())
          {
            this.isRole();
            const requestOptions = {
              method: "POST",
              body: JSON.stringify({ name: this.name, mail: this.mail, pwd: this.pwd, code: this.code, is_client: this.is_client, language: this.language })
            };
            fetch("https://babawallet.alwaysdata.net/log/save_account", requestOptions)
              .then(response => {
                if(!response.ok){
                  return response.json().then(json => Promise.reject(json)); 
                }
                return response.json();
              }) 
              .then(data => {
                this.$cookies.remove('mail');
                this.$cookies.set("token", data.token);
                this.$cookies.set("role", data.role);
                Swal.fire({
                  icon: 'success',
                  title: this.$t('alerts.good'),
                  text: this.$t('alerts.accountcreated'),
                })
                GlobalMethods.isAClient();
              })
              .catch(error => {
                GlobalMethods.errorApi(error.error);
              });
          }
        },
        /*Méthode permettant d'obtenir un code pour valider la création de compte à l'aide de GlobalMethods*/
        getCode(){
          if(this.mail)
          {
            this.$cookies.set("mail", this.mail);
            GlobalMethods.sendCode();
          }
          else{
            Swal.fire(this.$t("alerts.entermailcode"));
          }
      },
      /*Méthode qui permet de retourner à la page login en supprimant le mail des cookies si besoin*/
      back(){
        if(this.$cookies.isKey("mail")){
          this.$cookies.remove('mail');
        }
        this.$router.push("/");
      },
      /*Méthode permettant d'assigner les bonnes valeurs en fonction de la checkboxe*/
      isRole(){
        if(this.role == "Client")
        {
          this.is_client = true;
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
  height: 100vh;
}

  .create-form {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 600px;
    height: 700px;
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

  .texte {
    font-size: 20px;
    font-weight: bold;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 10px;
  }
  </style>