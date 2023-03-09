<template>
    <div class="main">
      <div class="header">
      <MainHeader text="BABA WALLET"/>
      </div>
      <div class="login-form">
        <form id="loginForm" method="post" @submit.prevent="post">
          <InputMain text="Mail" v-model="mail" type="mail"/>
          <InputMain text="Password" v-model="password" type="password"/>
          <GoButton text="Login" type="submit" :colore="'#34c98e'"/>
        </form>
        <div class="createbutton" @click.prevent.left="$router.push({name: 'createAccount'})">
        <GoButton text="Create an account" :colore="'#B1B9FC'"/>
        </div>
        <div class="forgotbutton" @click.prevent.left="goForgot()">
        <GoButton text="Forgotten password" :colore="'#B1B9FC'"/>
        </div>
        <button v-on:click="test1()">TEST1Client</button>
        <button v-on:click="test2()">TEST2Supplier</button>
        <button v-on:click="test3()">GlobalMethods</button>
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
        password: ''
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
            fetch("http://services-babawallet.alwaysdata.net:8300/log/check_account", requestOptions)
              .then(response => {
                  if(!response.ok){
                    if(response.status === 400){
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
                this.$cookies.set("token", data.token);
                GlobalMethods.isAClient(data.role);
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
            this.$router.push({name: 'forgottenPassword'});
          }
        },
        //Temp
        test1(){
          this.$cookies.set("role", "client");
          this.$cookies.set("token", 123456789);
          this.$router.push({ name: 'HomeClient' });
        },
        test2(){
          this.$cookies.set("role", "supplier");
          this.$cookies.set("token", 987654321);
          this.$router.push({ name: 'HomeSupplier' });
        },
        test3(){
          GlobalMethods.isAClient("client");
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
