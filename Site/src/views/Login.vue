<template>
    <div class="main">
      <div class="header">
      <MainHeader text="BABA WALLET"/>
      </div>
      <div class="login-form">
        <form id="loginForm" method="post" v-on:submit.prevent="post">
          <p>
            <label>Mail: </label>
            <input type="text" v-model="mail">
          </p>
          <p>
            <label>Password: </label>
            <input type="text" v-model="password">
          </p>
          <GoButton text="Login" type="submit" :color="'green'"/>
        </form>
        <GoButton text="Create an account" redirect="/createAccount" expr="change" :color="'#B1B9FC'"/>
        <GoButton text="Forgotten password" v-on:click="goForgot()"/>
      </div>
    </div>
  </template>

  <script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  import GlobalMethods from "@/components/GlobalMethods.vue";
  import Swal from 'sweetalert2';
  export default {
    name: "loginForm",
    components: {GoButton, MainHeader},
    data(){
      return{
        mail: '',
        password: '',
      }},
      methods: {
        /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up*/
        checkArgs(){
          if(!this.mail) Swal.fire("Please enter your mail");
          if(!this.password) Swal.fire("Please enter your password");
          else return true;
        },
        /*Méthode qui envoie le mail et le mot de passe vers l'api si les champs sont remplis 
          quand l'utilisateur clique sur login.
          Si la requête est incorrecte, il y a 2 possibilités :
            -L'api renvoie son propre message d'erreur (mot de passe incorrect, mail non trouvé)
            -Une erreur serveur dans ce cas, on n'affiche que le status associé
            Dans ces cas, il y aura une pop-up explicative*/
        post(){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "POST",
              body: JSON.stringify({ mail: this.mail, password: this.password })
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/check_account", requestOptions)
              .then(response => {
                  if(!response.ok){
                    if(response.status == 400){
                      const data = response.json();
                      GlobalMethods.methods.errorApi(data.error);
                      throw new Error(data.error);
                    }
                    else{
                      GlobalMethods.methods.errorApi(response.status);
                      throw new Error(response.status);
                    }
                  }
              }) 
              .then(data => {
                this.$cookies.set("token", data.token);
                GlobalMethods.methods.isAClient(data.role);
              })
              .catch(error => {
                console.error("Error", error);
              });
          }
        },
        /* Méthode permettant de vérifier si le champ "adresse mail" est bien rempli.
           Si c'est le cas, enregistre dans les cookies le mail et redirige vers la page
           ForgottenPassword.
           Sinon affichage d'une pop-up*/
        goForgot(){
          if(!this.mail) Swal.fire("Please enter your mail to reset the password !");
          else{
            this.$cookies.set("mail", this.mail);
            window.location.href = "/ForgottenPassword.vue";
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
  justify-content: space-evenly;
  height: 100vh;
}

  .login-form {
    background-color: #f2f2f2;
    padding: 50px;
    border-radius: 10px;
    width: 50%;
    left: 50%;
    transform: translate(-50%);
    position: absolute;
  }
  
  .login-form label {
    color: black;
  }
  
  
  .login-form form {
    display: flex;
    flex-direction: column;
  }
  
  .login-form label {
    margin-bottom: 10px;
  }
  
  textarea {
    resize: none;
    height: 80px;
    width: 200px;
  }
  
  .login-form input, .login-form textarea {
    padding: 10px;
    border: none;
    border-radius: 5px;
    margin-bottom: 20px;
  }
  
  .login-form input:focus, .login-form textarea:focus {
    outline: none;
    box-shadow: 0 0 5px #B1B9FC;
    transform: scale(1.05);
    transition: transform 0.3s ease-in-out;
  }
  
  </style>
