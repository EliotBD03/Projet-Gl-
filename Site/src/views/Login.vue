<template>
    <div class="main">
      <div class="header">
      <MainHeader text="BABA WALLET"/>
      </div>
      <div class="contact-form">
        <form id="addWallet" method="post" v-on:submit.prevent="post">
            <p>
                <label>Mail: </label>
                <input type="text" v-model="mail">
            </p>
            <p>
                <label>Password: </label>
                <input type="text" v-model="password">
            </p>
            <GoButton text="Login" type="submit"/> 
            <!--Attention gérer le log en fonction de s'il est client/fourni-->
            <!-- + Accepter le log seulement si tout est OK voir les erreurs API -->
            <!--Est-ce que je vérifie si le token est vide ou quoi pr ça ? -> vérif si error vide -->
            <!--Ajouter bouton Create an account et Forgotten password-->
        </form>
      </div>
    </div>
  </template>

  <script>
  import GoButton from "@/components/GoButton.vue";
  /*Gérer la déconnexion partout : /!\ seulement 401 */ 
  export default {
    name: "ContactForm",
    components: {GoButton},
    data(){
      return{
        mail: '',
        password: '',
        role: '',
        errorApi: ''
      }},
      methods: {
        checkArgs(){
          if(!this.mail) Swal.fire("Please enter your mail");
          if(!this.password) Swal.fire("Please enter your password");
          else return true;
        },
        post(){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "POST",
              body: JSON.stringify({ mail: this.mail, password: this.password })
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/check_account", requestOptions)
              .then(response => {
                  data = response.json();
                  if(!response.ok){
                    if(response.status != 500 ) //500 -> problème serveur et 401 -> juste problème token PAS DE MSG, 400->pb requête (on a un msg)
                    {
                      this.errorApi = data.error;
                    }
                    else
                    {
                      this.errorApi = response.status;
                    }
                    throw new Error(this.errorApi);
                  }
              }) 
              .then(data => {
                this.$cookies.set("token", data.token);
                this.role = data.role;
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

  .contact-form {
    background-color: #f2f2f2;
    padding: 50px;
    border-radius: 10px;
  }
  
  .contact-form label {
    color: black;
  }
  
  
  .contact-form form {
    display: flex;
    flex-direction: column;
  }
  
  .contact-form label {
    margin-bottom: 10px;
  }
  
  textarea {
    resize: none;
    height: 80px;
    width: 200px;
  }
  
  .contact-form input, .contact-form textarea {
    padding: 10px;
    border: none;
    border-radius: 5px;
    margin-bottom: 20px;
  }
  
  .contact-form input:focus, .contact-form textarea:focus {
    outline: none;
    box-shadow: 0 0 5px #B1B9FC;
    transform: scale(1.05);
    transition: transform 0.3s ease-in-out;
  }
  
  </style>