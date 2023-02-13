/*https://jasonwatmore.com/post/2020/04/30/vue-fetch-http-get-request-examples*/
new Vue({
    el: '#wallet',
    data(){
    return{
      listWallet: [],
      wallet : null
    }},
    //Get //token = ? (dans le lien) checkaccount faire .token
    created() {
        fetch("https://babawallet.alwaysdata.net:8300/api/client/?/wallets")
          .then(response => response.json())
          .then(data => (this.walletInfo = data.total)); //data.total modifier ?
    },
    methods: {
        seeMore(wallet){
          this.wallet = wallet;
        },
        notSeeMore(){
          this.wallet = null;
        }
      }
    });