<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.paiement"/>
        </div>
        <div class="qrcode">
            <canvas id="canvas"></canvas>
        </div>
        <p><b>{{ $t("invoices.pay") }}</b></p>
        <div class="bottombuttons">
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'darkblue'"/>
            </div>
            <div class="paybutton" @click.prevent.left="pay()">
                <GoButton text="button.pay" :colore="'#34c98e'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import QRCode from 'qrcode';
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from "sweetalert2";

export default {
    components: {
        MainHeader,
        GoButton
    },
    data() {
        return {
            invoiceId: sessionStorage.getItem("invoice_id"),
            proposal: sessionStorage.getItem("proposal"),
        }
    },
    mounted() {
        GlobalMethods.getCurrentLanguage();
        this.generatePayconiqQRCode();
    },
    methods: {
        // Génère le QR Code de paiement
        generatePayconiqQRCode() {
            const url = "https://www.youtube.com/watch?v=xvFZjo5PgG0"
            QRCode.toCanvas(document.getElementById('canvas'), url, function (error) {
                if (error) console.error(error);
            });
        },
        // Retourne à la page de la facture
        back() {
            sessionStorage.removeItem("price");
            sessionStorage.removeItem("proposal")
            this.$router.push({name: 'InvoiceFull'})
        },
        // Paye la facture
        pay(){
            let paid_price = this.proposal;
            const requestOptions = {
                method: 'POST',
                headers: {"Authorization": this.$cookies.get("token")},
                body: JSON.stringify({ invoice_id: this.invoiceId, price: parseFloat(paid_price) })
            };
            fetch("https://babawallet.alwaysdata.net/api/client/invoices/paid", requestOptions)
                .then(response => {
                    if (!response.ok)
                        throw new Error(response.error);
                    else {
                        Swal.fire({
                            icon: 'success',
                            title: this.$t("alerts.good"),
                            text: this.$t("alerts.invoicepaid"),
                        })
                        sessionStorage.removeItem("price");
                        sessionStorage.removeItem("proposal")
                        this.$router.push({name: 'InvoiceFull'})
                    }
                })
                .catch(error => {
                    if(error.message == "unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else {
                        GlobalMethods.errorApi(error.message);
                }
            });
        }
    }
}
</script>

<style scoped>
.main{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}
.bottombuttons{
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
    align-items: center;
    width: 50%
}
</style>