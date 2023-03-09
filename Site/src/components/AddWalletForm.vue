<template>
  <!--Voir si cela ne cause pas pb pour actualiser après ajout-->
  <div class="contact-form">
    <form id="addWallet" method="post" v-on:submit.prevent="post">
      <p>
        <InputMain text="Enter your name" v-model="name"/>
      </p>
      <p>
        <InputMain text="Enter your address" v-model="address"/>
      </p>
      <GoButton text="Add" type="submit" :colore="'gray'"/>
      <!-- <button class = "greenButton rightButton fixed" type="submit"> ADD </button>-->
    </form>
  </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
export default {
  name: "ContactForm",
  components: {InputMain, GoButton},
  data(){
    return{
      name: '',
      address: ''
    }},
  /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up*/
  methods: {
    checkArgs(){
      if(!this.name) Swal.fire("Please enter your name");
      if(!this.address) Swal.fire("Please enter your address");
      else return true;
    },
    post(){
      if(this.checkArgs())
      {
        const requestOptions = {
          method: "POST",
          headers: {'token': this.$cookies.get("token")},
          body: JSON.stringify({ name: this.name, address: this.address })
        };
        fetch("http://services-babawallet.alwaysdata.net:8300/api/client/wallets", requestOptions)
            .then(response => {
              if(!response.ok){
                if(response.status === 401){
                  this.$cookies.remove("role");
                  this.$cookies.remove("token");
                  Swal.fire('Your connection has expired');
                  this.$router.push("/");
                }
                else{
                  GlobalMethods.errorApi(response.status);
                  throw new Error(response.status);
                }
              }
            })
            .catch(error => {
              console.error(error);
            });
        this.$router.push("/wallets");
      }
    }
  }
}
</script>

<style scoped>
.contact-form {
  display: flex;
  align-items: center;
  justify-content: center;
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
  align-items: center;
  justify-content: center;
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
  transform: scale(1.05);
  transition: transform 0.3s ease-in-out;
}

</style>