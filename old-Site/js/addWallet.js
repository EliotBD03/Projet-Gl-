//Ne pas oublier gérer les erreurs (avec l'api)
new Vue({
    el: '#addWallet',
    data(){
    return{
      name: '',
      address: ''
    }},
    //Post //token = ? (dans le lien) checkaccount faire .token
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
    });
