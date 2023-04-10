<script>
  import Swal from 'sweetalert2';
  import router from '@/router/index'
  import VueCookies from 'vue-cookies';
  import i18n from "@/i18n";
  const cookies = VueCookies;
  
  export default {
      name: 'GlobalMethods',
      /**
       * Cette méthode affiche le message (d'erreur) dans une pop-up.
       *
       * @param error Le message à afficher.
       */
      errorApi(error) {
          Swal.fire({
              icon: 'error',
              title: i18n.t("alerts.error"),
              text: i18n.t(error)
          })
      },
      /**
       * Cette méthode permet de gérer le comportement du site lorsque le token arrive à expiration.
       */
      errorToken() {
          cookies.remove("token");
          cookies.remove("role");
          Swal.fire(i18n.t("alerts.connectionexpired"));
          router.push("/");
      },
      /* Méthode permettant de rediriger l'utilisateur en fonction de son rôle récupéré grâce aux cookies*/
      isAClient() {
          const role = cookies.get("role");
          if (role === "client") {
              router.push({name: "HomeClient"});
          } else {
              router.push({name: "HomeSupplier"});
          }
      },
      /**
       * Méthode permettant d'obtenir un (nouveau) code pour valider le changement de mot de passe ou la création de compte
       * @throws une erreur potentiellement renvoyée par l'API.
       */
      async sendCode() {
          const requestOptions = {
              method: "GET"
          };
          let response = null;
          try {
              response = await fetch("https://babawallet.alwaysdata.net/log/code?mail=" + cookies.get("mail"), requestOptions);
              if (!response.ok) {
                  const data = await response.json();
                  throw new Error(data.error);
              } else {
                  Swal.fire(i18n.t("alerts.mailsent"));
              }
          } catch (error) {
              this.errorApi(error.message);
          }
      },
      /**
       * Cette méthode permet la déconnexion de l'utilisateur.
       *
       * @param chemin La page sur laquelle l'utilisateur ira.
       * @throws une erreur potentiellement renvoyée par l'API.
       */
      disconnect(chemin) {
          const requestOptions = {
              method: "POST",
              headers: {'Authorization': cookies.get("token")}
          };
          fetch("https://babawallet.alwaysdata.net/log/disconnect", requestOptions)
              .then(response => {
                  if (!response.ok) {
                      if (response.status == 401) {
                          throw new Error("Token");
                      } else {
                          return response.json().then(json => Promise.reject(json));
                      }
                  } else {
                      cookies.remove("token");
                      cookies.remove("role");
                      Swal.fire(i18n.t("alerts.seeyousoon"));
                      router.push(chemin);
                  }
              })
              .catch(error => {
                  if (error.message === "Token") {
                      this.errorToken();
                  } else {
                      this.errorApi(error.error);
                  }
              });
      },
      async getCurrentLanguage() {
          const requestOptions = {
              method: "GET",
              headers: {'Authorization': cookies.get("token")}
          };
          try {
              const response = await fetch("https://babawallet.alwaysdata.net/api/common/languages/current_language", requestOptions);
              const currentLanguage = await response.json();
              i18n.locale = currentLanguage.language;
          } catch (error) {
              if (error.message === "Token") {
                  this.errorToken();
              }
              else {
                  this.errorApi(error.message);
              }
          }
      }
  }
  </script>

