<template>
    <div class="main">
      <div class="header">
      <MainHeader text="BABA WALLET"/>
      </div>
      <div class="forgot-form">
        <p>An email is sent : Follow the instructions</p>
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
          </form>
          <GoButton text="SEND A NEW CODE" v-on:click="sendCode()"/>
          <GoButton text="Back" v-on:click="back()" />
      </div>
    </div>
  </template>

<script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  import Swal from 'sweetalert2';
  export default {
    name: "forgotForm",
    components: {GoButton,MainHeader},
    data(){
      return{
        mailCode: '',
        newPassword: '',
        repeatedPassword: ''
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
                    if(response.status == 503 || response.status == 400){
                      const data = response.json();
                      this.errorApi(data.error);
                      throw new Error(data.error);
                    }
                    else{
                      this.errorApi(response.status);
                      throw new Error(response.status);
                    }
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
          try {
            response = await fetch("https://babawallet.alwaysdata.net:8300/log/code", requestOptions);
            if(!response.ok){
              if(response.status == 503 || response.status == 400){ //voir si Adrien garde l'erreur 400 -> mail dans la BDD?
                const data = await response.json();
                this.errorApi(data.error);
                throw new Error(data.error);
              }
              else{
                this.errorApi(response.status);
                throw new Error(response.status);
              }
            }
          } catch (error) {
            console.error(error);
          }
        },
        /*Retourner à la page login en supprimant le mail des cookies*/
        back(){
          this.$cookies.delete('mail');
          window.location.href = "/Login.vue";
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
    width: 50%;
    left: 50%;
    transform: translate(-50%);
    position: absolute;
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