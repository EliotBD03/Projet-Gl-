/*https://jasonwatmore.com/post/2020/04/30/vue-fetch-http-get-request-examples*/
new Vue({
  el: "#wallet",
  data: {
    listWallet: [
      { name: "Item 1", nameOwner: "BOb", address: "Rue ll", lastConsumptionOfWater: 10, lastConsumptionOfGas: 44, lastConsumptionOfElectricity: 90, listContracts: [{nom: "Engie", conso: "10000", prix : "400000"}, {nom: "paee", conso: "9000", prix : "5000"}]},
      
      { name: "Item 2", nameOwner: "Bobby", address: "Rue nn", lastConsumptionOfWater: 20, lastConsumptionOfGas: 30, lastConsumptionOfElectricity: 100, listContracts: [{nom: "Watersss", conso: "60000", prix : "9000"}]},
      
      { name: "Item 3", nameOwner: "Josh", address: "Rue pp", lastConsumptionOfWater: 50, lastConsumptionOfGas: 70, lastConsumptionOfElectricity: 700, listContracts: [{nom: "Pooppps", conso: "90000", prix : "4000"}]},
      
      { name: "Item 4", nameOwner: "nag", address: "Rue gg", lastConsumptionOfWater: 90, lastConsumptionOfGas: 30, lastConsumptionOfElectricity: 790, listContracts: [{nom: "Aht", conso: "450000", prix : "3000"}]},
    ]
  },
    methods: {
        seeMore(wallet){          
          sessionStorage.setItem('wallet', JSON.stringify(wallet));
          window.location.href = "../../html/client/walletFull.html";
        },
        getIndex(){
          let index = (item) => item.name == this.wallet.name;
          console.log('test 3:', this.listWallet.findIndex(index));
          return this.listWallet.findIndex(index);
        }
      }
    });

