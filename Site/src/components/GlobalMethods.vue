<script>
  import Swal from 'sweetalert2';
  import router from '@/router/index'
  import VueCookies from 'vue-cookies';
  const cookies = VueCookies;
  
  export default {
    name: 'GlobalMethods',
    /*Affiche le message d'erreur venant de l'api dans une pop-up*/
    errorApi(error){
      Swal.fire({
      icon: 'error',
      title: 'OH NO !',
      text: error
      })  
    },
    /* Méthode permettant de rediriger l'utilisateur en fonction de son rôle*/
    isAClient(){
      const role = cookies.get("role");
      if(role === "client"){
        router.push({name: "HomeClient"});
      } 
      else{
        router.push({name: "HomeSupplier"});
      }
    },
    /*Méthode permettant d'obtenir un (nouveau) code pour valider le changement de mot de passe ou la création de compte*/
    async sendCode(){
      const requestOptions = {
        method: "GET"
      };
      let response = null;
      try {
        response = await fetch("https://babawallet.alwaysdata.net/log/code?mail=" + cookies.get("mail"), requestOptions);
        if(!response.ok){
          const data = await response.json();
          throw new Error(data.error);
        }
        else{
          Swal.fire('A mail is sent');
        }
      } catch (error) {
        this.errorApi(error.message);
      }
    },
    /*Méthode qui permet la déconnexion de l'utilisateur*/
    disconnect(chemin){
      const requestOptions = {
        method: "POST",
        headers: {'Authorization' : cookies.get("token")}
      };
      fetch("https://babawallet.alwaysdata.net/log/disconnect", requestOptions)
        .then(response => {
          if(!response.ok){
            const data = response.text();
            //On vérifie cela car la promesse lorsqu'il s'agit d'un token expiré est vide
            if(response.status == 401 && data.trim() === ''){
              //trim permet de supprimer les espaces blancs au début et à la fin d'une chaîne de caractères.
                throw new Error("Token");
            }
            else{
              throw response.json();
            }
          }
          else{
            cookies.remove("token");
            cookies.remove("role");
            Swal.fire('See you soon !');
            router.push(chemin);
          }
        })
        .catch(error => {
          if (error.message === "Token") {
            cookies.remove("token");
            cookies.remove("role");
            Swal.fire('See you soon !');
            router.push("/");
          } 
          else {
            error.then(data => {
              this.errorApi(data.error);
            });
          }
        });
    }
  }
  </script>

