//Ne pas oublier gérer les erreurs (avec l'api)
new Vue({
    el: '#deleteWallet',
    methods: {
      //delete //token = ? (dans le lien) checkaccount faire .token
      delete() {
          const requestOptions = {
              method: "DELETE",
              headers: { "Content-Type": "application/json" },
            };
            fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets/:address", requestOptions)
              .then(response => response.json())
          
          //window.location.href = "../../html/client/wallet.html";
      }
    }
});