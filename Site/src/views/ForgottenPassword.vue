<template>
    <div class="main">
      <div class="header">
      <MainHeader text="BABA WALLET"/>
      </div>
      <div class="forgot-form">
        <form id="forgotForm" method="post" v-on:submit.prevent="post">
            <p>
              <label>Mail Code: </label>
              <input type="text" v-model="mailCode">
            </p>
            <p>
              <label>New Password: </label>
              <input type="text" v-model="newPassword">
            </p>
            <p>
              <label>Repeated Password: </label>
              <input type="text" v-model="repeatedPassword">
            </p>
            <GoButton text="Submit" type="submit"/> 
            <GoButton text="SEND A NEW CODE" v-on:click="sendCode()"/>
        </form>
      </div>
    </div>
  </template>

<script>
  import GoButton from "@/components/GoButton.vue";
  import Swal from 'sweetalert2';
  export default {
    name: "forgotForm",
    components: {GoButton},
    data(){
      return{
        mailCode: '',
        newPassword: '',
        repeatedPassword: '',
        errorApi1: '',
        errorApi2:'',
        flag: ''
      }},
      /*Récupère le mail dans les cookies et l'envoie vers l'api pour que l'utilisateur puisse avoir le code*/
      created(){
          this.sendCode();
      },
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up.
          Vérifie également si les mots de passe sont identiques*/
        checkArgs(){
          if(!this.mailCode) Swal.fire("Please enter your mail");
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
              body: JSON.stringify({ codeMail: this.codeMail, newPassword: this.newPassword })
            };
            fetch("https://babawallet.alwaysdata.net:8300/log/renitialize_pwd", requestOptions)
              .then(response => {
                  if(!response.ok){
                    this.flag = true;
                  }
              }) 
              .then(data => {
                if(this.flag){
                  this.flag = false
                  this.errorApi1 = data.error;
                  throw new Error(this.errorApi1);
                }
                else{
                  this.$cookies.delete('mail');
                  Swal.fire({
                      icon: 'success',
                      title: 'Good !',
                      text: 'Password changed !'
                    })
                  window.location.href = "/Login.vue";
                }
              })
              .catch(error => {
                console.error(error);
                this.errorApi(this.errorApi1);
              });
          }
        },
        /*Méthode permettant d'obtenir un (nouveau) code pour valider le changement de mot de passe*/
        async sendCode(){
          const requestOptions = {
            method: "GET",
            body: JSON.stringify({ mail: this.$cookies.get("mail") })
          };
          let response = null;
          let data = null;
          try {
            response = await fetch("https://babawallet.alwaysdata.net:8300/log/code", requestOptions);
            data = await response.json();
            if(!response.ok){
              this.errorApi2 = data.error;
              throw new Error(this.errorApi2);
            }
          } catch (error) {
            console.error(error);
            this.errorApi(this.errorApi2);
          }
        },
        /*Affiche le message d'erreur venant de l'api dans une pop-up*/
        errorApi(error){
          Swal.fire({
            icon: 'error',
            title: 'OH NO !',
            text: error
          })
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
  height: 100vh;
}

  .forgot-form {
    background-color: #f2f2f2;
    padding: 50px;
    border-radius: 10px;
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