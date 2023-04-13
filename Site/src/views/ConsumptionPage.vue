<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.consumption"/>
        </div>
        <div class="topbutton">
            <div @click.prevent.left="changeMode(false)">
                <GoButton text="button.table" :colore="'#34c98e'"/>
            </div>
            <div @click.prevent.left="exportData()">
                <GoButton text="button.export" :colore="'#34c98e'"/><!--trad-->
            </div>
            <input type="file" id="csv-file" accept=".csv"/>
            <div @click.prevent.left="importData()">
                <GoButton text="button.import" :colore="'#34c98e'"/><!--trad-->
            </div>
            <div @click.prevent.left="changeMode(true)">
                <GoButton text="button.graphic" :colore="'#34c98e'"/>
            </div>
        </div>
        <div class="middlebutton">
            <button class="arrow-button" @click.prevent.left="getDataBefore()">
                <span class="arrow">&larr;</span>
            </button>
            <div class="infos">
                <div class="container">
                    <canvas ref="myChart"></canvas>
                    <div class="tableH" id="table">
                        <div>
                            <div v-for="(date, index) in listDate" :key="index" class="cellule">
                                {{ date }}
                            </div>
                        </div>
                        <div>
                            <div v-for="(data, index) in listValue" :key="index" class="cellule">
                                {{ data }}
                            </div>
                        </div>
                    </div>
                </div>
                <div class="newconsumption" >
                    <InputMain type="date" id="dateNewConsumption" value="2020-01-01" min="2020-01-01" max="2099-12-31"/>
                    <InputMain type="number" id="dataNewConsumption" min="0" step="0.01" />
                    <div @click.prevent.left="post()">
                        <GoButton type="submit" text="button.add" :colore="'#34c98e'"/>
                    </div>
                </div>
            </div>
            <button class="arrow-button" @click.prevent.left="getDataAfter()">
                <span class="arrow">&rarr;</span>
            </button>
        </div>
        <div class="bottombutton">
            <div @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'darkblue'"/>
            </div>
            <div id="modeTime" @click.prevent.left="changeModTime()">
                <GoButton :text=labelButtonDisplay :colore="'#34c98e'"/><!--trad-->
            </div>
        </div>
    </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import Chart from "chart.js/auto";
import InputMain from "@/components/InputMain.vue";
import Swal from "sweetalert2";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Papa from "papaparse";
import VueCookies from 'vue-cookies';
const cookies = VueCookies;

export default {
    components: {
        GoButton,
        MainHeader,
        InputMain
    },

    data(){
        return{
            mode : true,
            ean : sessionStorage.getItem('ean'),
            date : "",
            isAfter : true,
            listDate : [],
            listValue : [],
            listValue2 : [],
            isComparaison : false,
            listNewValue : [],
            listNewDate : [],
            forcing : false,
            chart : null,
            isDisplayDay : true,
            month : "",
            year : "",
            unity : "",
            labelButtonDisplay : ""//trad
        }},

    created() {
        this.getUnity();
        this.getConsumption();
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        showData() { // Méthode qui permet d'afficher/actualiser les données
            this.mode ? this.showGraphic() : this.showTable();
        },

        changeMode (mode) { // Méthode qui permet de changer le mode d'affichage
            if(this.mode == mode) return;
            this.mode = mode;
            this.showData();
        },

        exportData() { // Méthode qui permet d'exporter les données actuelles affichées à l'écran. Le fichier est au format csv
            const table = [];
            table.push(["date", "data" + this.unity]);
            for (let i = 0; i < this.listValue.length; i++) {
                table.push([this.listDate[i], this.listValue[i]]);
            }

            const csv = Papa.unparse(table);
            const lien = document.createElement("a");
            lien.setAttribute("href", "data:text/csv;charset=utf-8," + encodeURIComponent(csv));
            lien.setAttribute("download", "table.csv");
            document.body.appendChild(lien);
            lien.click();
            document.body.removeChild(lien);
        },

        importData() { // Méthode qui permet d'importer des données via un fichier csv directement dans le base de données
            this.listNewDate = [];
            this.listNewValue = [];

            const dateRegex = /\d{4}-\d{2}-\d{2}/;

            const fileInput = document.getElementById('csv-file');
            if(fileInput.files.length > 0) {
                Papa.parse(fileInput.files[0] , {
                    complete: (results) => {
                        const tmp = results.data.slice(1);
                        for(let i = 0; i < tmp.length; i++) {
                            if(dateRegex.test(tmp[i][0])) {
                                this.listNewDate.push(tmp[i][0]);
                                this.listNewValue.push(tmp[i][1]);
                            }
                        }
                        this.post();
                    }
                });
            }
        },

        getDataBefore() { // Méthode qui permet de changer la date de recherche pour en suite aller chercher de nouvelles données dans le passé
            if(this.isDisplayDay) {
                this.isAfter = false;
                this.date = this.listDate[0];
                this.getConsumption();
            } else {
                this.month--;
                if(this.month <= 0) {
                    this.month = 12;
                    this.year--;
                }
                this.getConsumptionOfMonth();
            }
        },

        getDataAfter() { // Méthode qui permet de changer la date de recherche pour en suite aller chercher de nouvelles données dans le futur
            if(this.isDisplayDay) {
                this.isAfter = true;
                this.date = this.listDate.slice(-1)[0];
                this.getConsumption();
            } else {
                this.month++;
                if(this.month >= 13) {
                    this.month = 1;
                    this.year++;
                }
                this.getConsumptionOfMonth();
            }
        },

        showTable() { // Méthode qui détruit le graphique pour afficher ensuite le tableau
            if(!this.mode) {
                this.chart.destroy();
                document.getElementById("table").style.display = "flex";
            }
        },

        showGraphic() { // Méthode qui cache le tableau pour en suite préparer et afficher le graphique
            if(this.mode) {
                document.getElementById("table").style.display = "none";

                const ctx = this.$refs.myChart.getContext('2d');
                const data = {
                    labels: this.listDate,
                    datasets: [{
                        label: this.$t("consumptions.yourconsumption") + this.unity,//trad
                        data: this.listValue,
                        fill: false,
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }]
                };

                if(this.isComparaison) {
                    data.datasets[0] += {
                        label: this.$t("consumptions.otherconsumption"),//trad
                        data: this.listValue2,
                        fill: false,
                        borderColor: 'rgb(192, 75, 192)',
                        tension: 0.1
                    }
                }

                if(this.chart) {
                    this.chart.destroy();
                }

                this.chart = new Chart(ctx, {
                    type: 'line',
                    data: data,
                });

                this.chart.update();
            }
        },

        checkArgs() { // Méthode qui permet de vérifier que l'utilisateur a bien entré toutes les données néccéssaire pour ajouter une valeur
            if(this.listNewDate.length > 0 && this.listNewValue.length == this.listNewDate.length) {
                return true;
            }

            const date = document.getElementById("dateNewConsumption").value;
            const data = document.getElementById("dataNewConsumption").value;
            if(data == '')
            {
                Swal.fire(this.$t("alerts.entervalue"));//trad
                return false;
            }
            else if(data < 0)
            {
                Swal.fire(this.$t("alerts.enterpositiveconsumption"));//trad
                return false;
            }

            this.listNewDate.push(date);
            this.listNewValue.push(data);

            return true;
        },

        async getUnity() { // Méthode qui permet de savoir qu'elle est l'unité de mesure en faisant une requête vers l'API
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")},
            };
            try {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/contracts/type_of_energy/ + ${sessionStorage.getItem('contractId')}`, requestOptions);
                if (!response.ok) {
                    const data = await response.text();
                    if(response.status == 401 && data.trim() === ''){
                        throw new Error("Token");
                    }
                    else{
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                } else {
                    const data = await response.json();

                    if(data.type_of_energy == "water") {
                        this.unity = " (m³)";
                    } else {
                        this.unity = " (kWh)";
                    }

                    this.labelButtonDisplay = this.$t("consumptions.displaymonth") + this.unity;
                }
            } catch(error) {
                if(error.message === "Token") {
                    this.$cookies.remove("token");
                    this.$cookies.remove("role");
                    Swal.fire(this.$t("alerts.connectionexpired"));//trad
                    this.$router.push("/");
                }
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },

        async getConsumption() { // Méthode qui permet de récolter les données demandées en faisant une requête vers l'API
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")},
            };
            try {
                let dateWay = "";
                if(this.date != "") {
                    dateWay = `&date=${this.date}`;
                }
                else {
                    this.isAfter = false;
                }
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/consumptions/${this.ean}?is_after=${this.isAfter}${dateWay}`, requestOptions);
                if (!response.ok) {
                    const data = await response.text();
                    if(response.status == 401 && data.trim() === ''){
                        throw new Error("Token");
                    }
                    else{
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                } else {
                    const data = await response.json();

                    if(!(Object.keys(data.listConsumption).length === 0)) {
                        let keys = Object.keys(data.listConsumption);
                        let values = Object.values(data.listConsumption);

                        if(this.isAfter) {
                            this.listDate = this.listDate.concat(keys);
                            this.listValue = this.listValue.concat(values);

                            if(this.listDate.length > 30) {
                                this.listDate = this.listDate.slice(10);
                                this.listValue = this.listValue.slice(10);
                            }
                        } else {
                            this.listDate = keys.concat(this.listDate);
                            this.listValue = values.concat(this.listValue);

                            if(this.listDate.length > 30) {
                                this.listDate = this.listDate.slice(0, 30);
                                this.listValue = this.listValue.slice(0, 30);
                            }
                        }
                    }

                    this.showData();
                }
            } catch(error) {
                if(error.message === "Token") {
                    GlobalMethods.errorToken();
                }
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },

        async getConsumptionOfMonth() { // Méthode qui permet de récolter les données demandées en faisant une requête vers l'API
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")},
            };
            try {
                let dateWay = "";
                if(this.year != "" && this.month != "") {
                    dateWay = `?year=${this.year}&month=${this.month}`;
                }
                console.log(dateWay);
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/consumptions_month/${this.ean}${dateWay}`, requestOptions);
                if (!response.ok) {
                    const data = await response.text();
                    if(response.status == 401 && data.trim() === ''){
                        throw new Error("Token");
                    }
                    else{
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                } else {
                    const data = await response.json();

                    this.listDate = Object.keys(data.listConsumption);
                    this.listValue = Object.values(data.listConsumption);

                    if(this.listDate.length > 0) {
                        this.year = this.listDate[0].slice(0, 4);
                        this.month = this.listDate[0].slice(5, 7);
                    }

                    this.showData();
                }
            }  catch(error) {
                if(error.message === "Token") {
                    GlobalMethods.errorToken();
                }
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },

        async post (){ // Méthode qui permet d'ajouter des données de consommations
            if(this.checkArgs())
            {
                const requestOptions = {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({ ean: this.ean, list_value: this.listNewValue, list_date: this.listNewDate, forcing: this.forcing})
                };
                try {
                    const response = await fetch(`https://babawallet.alwaysdata.net/api/common/consumptions`, requestOptions)
                    if (!response.ok) {
                        if(response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = await response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        const data = await response.json();
                        if(data.valueChange) {
                            Swal.fire({
                                icon: 'success',
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.addedconsumption")
                            })
                            this.listNewValue = [];
                            this.listNewDate = [];

                            this.listDate = [];
                            this.listValue = [];

                            if(this.isDisplayDay) {
                                this.date = "";
                                this.getConsumption();
                            } else {
                                this.year = "";
                                this.month = "";
                                this.getConsumptionOfMonth();
                            }
                        } else {
                            Swal.fire({
                                title: this.$t("alerts.surechangingvalue"),
                                text: this.$t("alerts.cantreturn"),
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonColor: '#3085d6',
                                cancelButtonColor: '#d33',
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    this.forcing = true;
                                    this.post();
                                    this.forcing = false;
                                }
                            })
                        }
                    }
                } catch(error) {
                    if(error.message === "Token") {
                        GlobalMethods.errorToken();
                    }
                    else {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            }
        },

        changeModTime() { // Méthode qui permet de changer le type d'affichage. Soit jour par jour, soit par moi
            this.isDisplayDay = !this.isDisplayDay;
            this.listDate = [];
            this.listValue = [];

            if(this.isDisplayDay) {
                this.labelButtonDisplay = this.$t("consumptions.displaymonth") + this.unity;//trad
                this.getConsumption();
            } else {
                this.labelButtonDisplay = this.$t("consumptions.displayday") + this.unity;//trad
                this.getConsumptionOfMonth();
            }
        },

        back() { // Méthdode qui permet de revenir à la page précédente en fonction de son rôle
            if(cookies.isKey("token") && cookies.isKey("role"))
            {
                if(cookies.get("role") === 'client'){
                    sessionStorage.clear();
                    this.$router.push('/wallets');
                }
                else{
                    sessionStorage.removeItem('ean');
                    sessionStorage.removeItem('contractId');
                    this.$router.push('/contractFull');
                }
            }
        }
    }
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    height: 100vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 20vh;
    z-index: 9999;
}
.topbutton {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    margin-top: 20px;
}

.infos {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    padding: 0 50px;
    margin-top: 25px;
}

.container {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 1000px;
    height: 400px;
    background: rgb(236, 236, 236);
    box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
}

.newconsumption {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-evenly;
    width: 1000px;
    height: 70px;
    background: rgb(236, 236, 236);
    box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
    margin: 10px;
    font-family: Avenir, Helvetica, Arial, sans-serif;
    font-size: 30px;
}

.bottombutton {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 25px;
}

.middlebutton {
    display: flex;
    align-items: center;
    padding: 0 50px;
    margin-top: 25px;
}
.tableH{
    display: flex;
    height: 300px;
    width: 700px;
    overflow-x: auto;
    flex-direction: row;
}

.cellule {
    text-align: center;
    border: 1px solid black;
    padding: 25px;
    font-weight: 1000;
    font-size: 17px;
    width: 150px;
}

.arrow-button {
    padding: 10px 20px;
    background: #e8e8e8;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    align-items: center;
    justify-content: center;
    height: 100px;
    width: 150px;
}

.arrow {
    font-size: 44px;
    margin-left: 10px;
    color: #34c98eff;
}
</style>
