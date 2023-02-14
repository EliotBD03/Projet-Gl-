/*https://jasonwatmore.com/post/2020/04/30/vue-fetch-http-get-request-examples*/
new Vue({
    el: '#wallet',
    data(){
    return{
      listWallet: [],
      wallet : JSON.parse(sessionStorage.getItem('wallet'))
    }},
    //Get //token = ? (dans le lien) checkaccount faire .token
    created() {
        fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets")
          .then(response => response.json())
          .then(data => (this.listWallet = data.total));
    },
    methods: {
        seeMore(wallet){
          this.wallet = sessionStorage.setItem('wallet', JSON.stringify(wallet));
          window.location.href = "../../html/client/walletFull.html";
        },
        getIndex(){
          let index = (item) => item.name == this.wallet.name;
          //console.log('test:', this.listWallet.findIndex(index));
          return this.listWallet.findIndex(index);
        }
      }
    });