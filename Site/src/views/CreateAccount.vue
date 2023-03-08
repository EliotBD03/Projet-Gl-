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
        <p>
          <label>Mail: </label>
          <input type="text" v-model="mail">
        </p>
        <p>
          <label>Password: </label>
          <input type="text" v-model="password">
        </p>
        <p>
          <label>Repeated Password: </label>
          <input type="text" v-model="repeatedPassword">
        </p>
        <p>
          <label>Mail Code: </label>
          <input type="text" v-model="code">
        </p>
        <GoButton text="Create an account" type="submit" :colore="'green'"/>
      </form>
      <GoButton text="Send a code" @click.prevent.left="getCode()" :colore="'#B1B9FC'"/>
      <GoButton text="Back" @click.prevent.left="back()" :colore="'#B1B9FC'"/>
      <button v-on:click="selected()" >ICI</button> <!--Pour le test-->
    </div>
  </div>
</template>

  <script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  import GlobalMethods from "@/components/GlobalMethods.vue";
  import Swal from 'sweetalert2';
  export default {
    name: "createForm",
    components: {GoButton, MainHeader},
    data(){
      return{
        mail: '',
        password: '',
        repeatedPassword: '',
        code: '',
        language: '',
        selectedList:[],
        role: '',
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
          if(this.checkArgs() && this.selected())
          {
            const requestOptions = {
              method: "POST",
              body: JSON.stringify({ mail: this.mail, password: this.password, code: this.code, isClient: this.isClient, language: this.language })
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/check_account", requestOptions)
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
                this.$cookies.delete('mail');
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
          this.$cookies.delete('mail');
        }
        this.$router.push("/");
      },
      /*Méthode permettant de vérifier si les checkboxes sont cochées correctement et 
        assigner les bonnes valeurs en fonction*/
      selected(){
        if(this.role != '' && this.language != '')
        {
          if(this.role == "Client")
          {
            this.isClient = true;
          }
          return true;
        }
        else{
          if(this.role == ''){
            Swal.fire("Please make a choice between Client and Supplier !");
          }
          if(this.language == ''){
            Swal.fire("Please make a choice between English and Français !");
          }
          return false;
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

  .create-form {
    background-color: #f2f2f2;
    padding: 10px;
    border-radius: 10px;
    width: 50%;
    left: 50%;
    transform: translate(-50%);
    position: absolute;
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