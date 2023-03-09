<template>
  <div class="main">
    <div class="header">
    <MainHeader text="BABA WALLET"/>
    </div>
    <div class="create-form">
      <div class="line">
        <input type="radio" id="Client" value="Client" v-model="role">
        <label for="Client">Client</label>
        <br>
        <input type="radio" id="Supplier" value="Supplier" v-model="role">
        <label for="Supplier">Supplier</label>
        <br>
      </div>
      <div class="line">
        <input type="radio" id="français" value="français" v-model="language">
        <label for="français">Français</label>
        <br>
        <input type="radio" id="english" value="english" v-model="language">
        <label for="english">English</label>
        <br>
      </div>
      <form id="createForm" method="post" v-on:submit.prevent="post">
        <InputMain text="Mail" type="mail" v-model="mail"/>
        <InputMain text="Password" type="password" v-model="password"/>
        <InputMain text="Repeated password" type="password" v-model="repeatedPassword"/>
        <InputMain text="Code" type="text" v-model="code"/>
        <GoButton text="Create an account" type="submit" :colore="'#34c98e'"/>
      </form>
      <div @click.prevent.left="getCode()">
        <GoButton text="Send a code" :colore="'#B1B9FC'"/>
      </div>
      <div @click.prevent.left="back()">
        <GoButton text="Back" :colore="'#B1B9FC'"/>
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
        mail: '',
        password: '',
        repeatedPassword: '',
        code: '',
        language: 'english',
        selectedList:[],
        role: 'Client',
        isClient: false
      }},
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up*/
        checkArgs(){
          if(!this.code) Swal.fire("Please enter your mail");
          if(!this.newPassword) Swal.fire("Please enter your new password");
          if(!this.repeatedPassword) Swal.fire("Please enter your repetead password");
          if(this.repeatedPassword !== this.newPassword) Swal.fire("Passwords must be identical");
          else return true;
        },
        /*Méthode qui envoie le mail, le code reçu par mail, le nouveau mot de passe
          et s'il s'agit d'un client vers l'api si checkArgs() est true quand l'utilisateur 
          clique sur create an account.
          Si la requête est incorrecte, l'api renvoie un message d'erreur
          Si elle est correcte affiche une pop-up de succès et redirige*/
        post(){
          if(this.checkArgs())
          {
            this.isRole();
            const requestOptions = {
              method: "POST",
              body: JSON.stringify({ mail: this.mail, password: this.password, code: this.code, isClient: this.isClient, language: this.language })
            };
            fetch("https://babawallet.alwaysdata.net:8300/log/save_account", requestOptions)
              .then(response => {
                  if(!response.ok){
                    if(response.status == 400 || response.status == 503){
                      const data = response.json();
                      GlobalMethods.errorApi(data.error);
                      throw new Error(data.error);
                    }
                    else{
                      GlobalMethods.errorApi(response.status);
                      throw new Error(response.status);
                    }
                  }
              }) 
              .then(data => {
                this.$cookies.remove('mail');
                this.$cookies.set("token", data.token);
                Swal.fire({
                  icon: 'success',
                  title: 'Good !',
                  text: 'Account created !'
                })
                GlobalMethods.isAClient(data.role);
              })
              .catch(error => {
                console.error("Error", error);
              });
          }
        },
        /*Méthode permettant d'obtenir un code pour valider le création de compte*/
        getCode(){
          if(this.mail)
          {
            this.$cookies.set("mail", this.mail);
            GlobalMethods.sendCode();
          }
          else{
            Swal.fire("Please enter your mail to get a code !");
          }
      },
      /*Retourner à la page login en supprimant le mail des cookies si besoin*/
      back(){
        if(this.$cookies.isKey("mail")){
          this.$cookies.remove('mail');
        }
        this.$router.push("/");
      },
      /*Méthode permettant de vérifier si les checkboxes sont cochées correctement et 
        assigner les bonnes valeurs en fonction*/
      isRole(){
        if(this.role == "Client")
        {
          this.isClient = true;
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
}

.main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 110vh;
}

  .create-form {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 600px;
    height: 710px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 20px 20px 60px #bebebe,
    -20px -20px 60px #ffffff;
  }
  
  .create-form label {
    color: black;
  }
  
  
  .create-form form {
    display: flex;
    flex-direction: column;
  }
  
  .create-form label {
    margin-bottom: 10px;
  }
  
  textarea {
    resize: none;
    height: 80px;
    width: 200px;
  }
  
  .create-form input, .create-form textarea {
    padding: 10px;
    border: none;
    border-radius: 5px;
    margin-bottom: 20px;
  }
  
  .create-form input:focus, .create-form textarea:focus {
    outline: none;
    box-shadow: 0 0 5px #B1B9FC;
    transform: scale(1.05);
    transition: transform 0.3s ease-in-out;
  }
  
  .line{
    display: flex;
    flex-wrap: nowrap;
    justify-content: center;
    align-items: center;
  }
  </style>