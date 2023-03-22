<template>
  <!--Non fini-->
    <div class="main">
      <div class="header">
      <MainHeader text="Add a client"/>
      </div>
    </div>
    </template>
    
    <script>
    //import GoButton from "@/components/GoButton.vue";
    import GlobalMethods from "@/components/GlobalMethods.vue";
    import Swal from 'sweetalert2';
    import MainHeader from "@/components/MainHeader.vue";
    export default {
      name: "ContactForm",
      components: { MainHeader},
      data(){
        return{
          name: '',
          address: ''
        }},
      methods: {
        checkArgs(){
        },
        /*Méthode qui, si checkArgs() est true,*/
        post(){
          if(this.checkArgs())
          {
            const requestOptions = {
              method: "POST",
              headers: {'Authorization': this.$cookies.get("token")},
              body: JSON.stringify({ })
            };
            fetch("https://babawallet.alwaysdata.net/api/", requestOptions)
                .then(response => {
                  if(!response.ok){
                    const data = response.text();
                    if(response.status == 401 && data.trim() === ''){
                        throw new Error("Token");
                    }
                    else{
                      return response.json().then(json => Promise.reject(json)); 
                    }
                  }
                  else{
                    Swal.fire({
                        icon: 'success',
                        title: 'Good !',
                        text: 'Client created !'
                    })
                    this.$router.push({ name: 'Clients' });
                  }
                })
                .catch(error => {
                  if (error.message === "Token") {
                  this.$cookies.remove("token");
                  this.$cookies.remove("role");
                  Swal.fire('Your connection has expired');
                  this.$router.push("/");
                  } 
                  else {
                    GlobalMethods.errorApi(error.error);
                  }
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
      justify-content: center;
      align-items: center;
      height: 100vh;
    }
    .contact-form {
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f2f2f2;
      padding: 50px;
      border-radius: 10px;
      box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    }
    
    </style>
  