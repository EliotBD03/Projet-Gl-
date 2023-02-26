<template>
  <!--Voir si cela ne cause pas pb pour actualiser après ajout-->
  <div class="contact-form">
    <form id="addWallet" method="post" v-on:submit.prevent="post">
        <p>
          <label>Name: </label>
          <input type="text" v-model="name">
        </p>
        <p>
          <label>Address: </label>
          <input type="text" v-model="address">
        </p>
      <GoButton text="Add" type="submit"/> 
    </form>
  </div>
  
</template>
<script>
import GoButton from "@/components/GoButton.vue";

export default {
  name: "ContactForm",
  components: {GoButton},
  data(){
    return{
      name: '',
      address: ''
    }},
    //Post //token = ? checkaccount faire .token -> regarder le token dans header.
    methods: {
      checkArgs(){
        if(!this.name) alert("Please enter your name");
        if(!this.address) alert("Please enter your address");
        else return true;
      },
      post(){
        if(this.checkArgs())
        {
          const requestOptions = {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: this.name, address: this.address })
          };
          fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets", requestOptions)
            .then(response => response.json())
          
          //window.location.href = "../../html/client/wallet.html";
        }
      }
  }
}
</script>

<style scoped>
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