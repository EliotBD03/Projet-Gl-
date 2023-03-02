<template>
    <!-- Regarder pour mettre le formulaire globalement en css-->
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
            <GoButton text="Login" type="submit"/> 
            <GoButton text="Create an account" redirect= "/createAccount"/>
            <GoButton text="Forgotten password" v-on:click="goForgot()"/>
        </form>
      </div>
    </div>
  </template>

  <script>
  import GoButton from "@/components/GoButton.vue";
  export default {
    name: "loginForm",
    components: {GoButton},
    data(){
      return{
        mail: '',
        password: '',
        role: '',
        errorApi: ''
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
                    if(response.status != 400)
                    {
                      this.errorApi = response.status;
                    }
                    else
                    {
                      data = response.json();
                      this.errorApi = data.error;
                    }
                    throw new Error(this.errorApi);
                  }
                  else{
                    data = response.json();
                  }
              }) 
              .then(data => {
                this.$cookies.set("token", data.token);
                this.role = data.role;
                isClient();
              })
              .catch(error => {
                console.error("Error", error);
                Swal.fire({
                    icon: 'error',
                    title: 'OH NO !',
                    text: this.errorApi
                  })
              });
          }
        },
        /* Méthode permettant de rediriger l'utilisateur en fonction de son rôle*/
        isClient(){
          if(this.role == "client"){
            window.location.href = "/HomePage.vue"; //HomePageClient.vue
          } 
          //window.location.href = "/HomePageProvider.vue"
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