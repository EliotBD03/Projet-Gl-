new Vue({
    el: '#addWallet',
    data(){
    return{
      name: '',
      address: '',
      listWallet: [
        { name: "Item 1", nameOwner: "BOb", address: "Rue ll", lastConsumptionOfWater: 10, lastConsumptionOfGas: 44, lastConsumptionOfElectricity: 90, listContracts: [{nom: "Engie", conso: "10000", prix : "400000"}, {nom: "paee", conso: "9000", prix : "5000"}]},
        
        { name: "Item 2", nameOwner: "Bobby", address: "Rue nn", lastConsumptionOfWater: 20, lastConsumptionOfGas: 30, lastConsumptionOfElectricity: 100, listContracts: [{nom: "Watersss", conso: "60000", prix : "9000"}]},
      ]
    }},
    methods: {
        checkArgs(){
          if(!this.name) alert("Please enter your name");
          if(!this.address) alert("Please enter your address");
          else return true;
        }, //l'idée était juste de voir si cela fonctionnait :)
        addWallet(name, address){
          if (this.checkArgs()) {
            this.listWallet.push(name + address);
            console.log(this.listWallet[2]);}
        }
      }
    });
