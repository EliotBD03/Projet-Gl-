new Vue({
  el: "#walletFull",
  data: {
    wallet : JSON.parse(sessionStorage.getItem('wallet'))
  },
    created(){
        console.log('OK?: ', this.wallet);
    }
});
