<script>
  import Swal from 'sweetalert2';
  import router from '@/router/index'
  export default {
    name: 'GlobalMethods',
    methods: {
      /*Affiche le message d'erreur venant de l'api dans une pop-up*/
      errorApi(error){
        Swal.fire({
        icon: 'error',
        title: 'OH NO !',
        text: error
        }) 
      } 
    },
    /* Méthode permettant de rediriger l'utilisateur en fonction de son rôle*/
    isAClient(role){
      if(role == "client"){
        router.push({name: "HomeClient"}); //ou utiliser name: A voir avec les tests
      } 
      else{
        router.push({name: "HomeSupplier"});
      }
    },
    /*Méthode permettant d'obtenir un (nouveau) code pour valider le changement de mot de passe ou la création de compte*/
    async sendCode(){
      const requestOptions = {
        method: "GET",
        body: JSON.stringify({ mail: this.$cookies.get("mail") })
      };
      let response = null;
      try {
        response = await fetch("http://services-babawallet.alwaysdata.net:8300/log/code", requestOptions);
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
    /*Méthode qui permet la déconnexion de l'utilisateur*/
    disconnect(chemin){
      const requestOptions = {
        method: "POST",
        headers: this.$cookies.get("token")
      };
      fetch("http://services-babawallet.alwaysdata.net:8300/log/disconnect", requestOptions)
        .then(response => {
          if(!response.ok){
            if(response.status == 401){
              this.$cookies.remove("token");
              this.$cookies.remove("role");
              Swal.fire('Your connection has expired');
              router.push("/");
            }
            else{
              this.errorApi(response.status);
              throw new Error(response.status);
            }
          }
          else{
            this.$cookies.remove("token");
            this.$cookies.remove("role");
            Swal.fire('See you soon!');
            router.push(chemin);
          }
        })
        .catch(error => {
          console.error("Error", error);
        });
    }
  }
  </script>