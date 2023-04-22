<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.invoice"/>
        </div>
        <div class="container">
            <div class="infos">
                <p><b>{{ $t("invoice.associatedcontract") }}</b></p>
                <p>{{ invoice.contractId }}</p>
                <p><b>{{ $t("invoice.annualamount") }}</b></p>
                <p>{{ invoice.annualAmount }}</p>
                <p><b>{{ $t("invoice.mensualamount") }}</b></p>
                <p>{{ invoice.mensualAmount }}</p>
                <p><b>{{ $t("invoice.method") }}</b></p>
                <p>{{ invoice.method }}</p>
                <p><b>{{ $t("invoice.remaining") }}</b></p>
                <p>{{ invoice.remaining }}</p>
            </div>
            <div class="buttons">
                <div class="changeproposal">
                    <GoButton text="button.changeproposal" :colore="'red'"/>
                </div>
                <div class="changemethod">
                    <GoButton text="button.changemethod" :colore="'red'"/>
                </div>
                <div class="viewinfos">
                    <GoButton text="button.viewinfos" :colore="'red'"/>
                </div>
            </div>
        </div>
        <div class="bottombuttons">
            <div class="homebutton" @click.prevent.left="redirecting()">
                <GoButton text="header.home" :colore="'#B1B9FC'"/>
            </div>
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'darkblue'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";

export default {
    components: {
        MainHeader,
        GoButton
    },
    data() {
        return {
            invoice_id: sessionStorage.getItem("invoice_id")
        }
    },
    created() {
        this.getInvoice();
    },
    methods: {
        async getInvoice() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            };
            try {
                const response = await fetch("https://babawallet.alwaysdata.net/api/client/invoices/" + this.invoice_id, requestOptions);
                if(!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                } else {
                    const data = await response.json()
                    this.invoice = data.invoice;
                }
            } catch(error) {
                if (error.error === "error.unauthorizedAccess") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        }
    }
}
</script>