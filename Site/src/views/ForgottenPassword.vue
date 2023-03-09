<template>
    <div class="main">
      <div class="header">
      <MainHeader text="Password"/>
      </div>
      <div class="forgot-form">
        <p>An email is sent : Follow the instructions</p>
        <form id="forgotForm" method="post" v-on:submit.prevent="post">
            <InputMain text="Mail code" type="text" v-model="code"/>
            <InputMain text="New password" type="password" v-model="newPassword"/>
            <InputMain text="Repeated password" type="password" v-model="repeatedPassword"/>
            <GoButton text="Submit" type="submit" :colore="'#34c98e'"/>
          </form>
          <div @click.prevent.left="getCode()">
          <GoButton text="Send a new code" :colore="'gray'"/>
          </div>
          <div @click.prevent.left="back()">
          <GoButton text="Back" :colore="'gray'"/>
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
    name: "forgotForm",
    components: {InputMain, GoButton,MainHeader},
    data(){
      return{
        code: '',
        newPassword: '',
        repeatedPassword: ''
      }},
      /*Récupère le mail dans les cookies et l'envoie vers l'api pour que l'utilisateur puisse avoir le code*/
      created(){
          this.getCode();
      },
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up.
          Vérifie également si les mots de passe sont identiques*/
        checkArgs(){
          if(!this.code) Swal.fire("Please enter your mail");
          if(!this.newPassword) Swal.fire("Please enter your new password");
          if(!this.repeatedPassword) Swal.fire("Please enter your repetead password");
          if(this.repeatedPassword != this.newPassword) Swal.fire("Passwords must be identical");
          else return true;
        },
        /*Méthode qui envoie le code reçu par mail et le nouveau mot de passe vers l'api si checkArgs() 
          est true quand l'utilisateur clique sur submit.
          Si la requête est incorrecte, l'api renvoie un message d'erreur
          Si elle est correcte affiche une pop-up de succès et redirige*/
        post(){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "PUT",
              body: JSON.stringify({ code: this.code, newPassword: this.newPassword })
            };
            fetch("http://services-babawallet.alwaysdata.net:8300/log/renitialize_pwd", requestOptions)
              .then(response => {
                  if(!response.ok){
                    if(response.status == 503 || response.status == 400){
                      const data = response.json();
                      GlobalMethods.errorApi(data.error);
                      throw new Error(data.error);
                    }
                    else{
                      GlobalMethods.errorApi(response.status);
                      throw new Error(response.status);
                    }
                  }
                  else{
                    this.$cookies.remove('mail');
                    Swal.fire({
                        icon: 'success',
                        title: 'Good !',
                        text: 'Password changed !'
                      })
                    this.$router.push("/");
                  }
              }) 
              .catch(error => {
                console.error(error);
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
    box-shadow: 20px 20px 60px #bebebe,
    -20px -20px 60px #ffffff;
  }
  
  .forgot-form label {
    color: black;
  }
  
  
  .forgot-form form {
    display: flex;
    flex-direction: column;
  }
  
  .forgot-form label {
    margin-bottom: 10px;
  }
  
  textarea {
    resize: none;
    height: 80px;
    width: 200px;
  }
  
  .forgot-form input, .forgot-form textarea {
    padding: 10px;
    border: none;
    border-radius: 5px;
    margin-bottom: 20px;
  }
  
  .forgot-form input:focus, .forgot-form textarea:focus {
    outline: none;
    box-shadow: 0 0 5px #B1B9FC;
    transform: scale(1.05);
    transition: transform 0.3s ease-in-out;
  }
  
  </style>