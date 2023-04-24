<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.invoice"/>
        </div>
        <div class="allcards">
            <div class="cards" v-for="invoice in invoicesList" :key="invoice.id">
                <p class="titre"> {{ $t("invoices.invoicetitle", {number: invoice.invoiceId}) }}</p>
                <p><b>{{ $t("invoices.annualprice") }}</b></p>
                <p>{{ invoice.price }} €</p>
                <div v-if="invoice.status">
                    <div class="payed">
                        <GoButton text="button.payed" :colore="'orange'"/>
                    </div>
                </div>
                <div v-else>
                    <div class="notpayed" @click.prevent.left="seeMore(invoice)">
                        <GoButton text="button.go" :colore="'#34c98e'"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="bottombuttons">
            <div class="homebutton" @click.prevent.left="redirecting()">
                <GoButton text="header.home" :colore="'#B1B9FC'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import Swal from "sweetalert2";
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
    components: {
        MainHeader,
        GoButton
    },

    data() {
        return {
            nbr: 1,
            loading: false,
            lastPage: 0,
            invoicesList: []
        }
    },

    created() {
        this.getInvoices();
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        async getInvoices() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            };
            this.loading = true;
            try {
                const response = await fetch("https://babawallet.alwaysdata.net/api/client/invoices/page?page=" + this.nbr + "&limit=3", requestOptions);
                if (!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                } else {
                    const data = await response.json();
                    this.lastPage = data.last_page;
                    if (this.lastPage == 0) {
                        this.loading = true;
                        Swal.fire(this.$t("alerts.noinvoice"));
                    } else if (this.lastPage >= this.nbr) {
                        this.invoicesList.push(data.invoices);
                        this.invoicesList = this.invoicesList.flat();
                        this.loading = false;
                    }
                }
            } catch (error) {
                if (error.error === "error.unauthorizedAccess")
                    GlobalMethods.errorToken();
                else {
                    if (this.nbr === 1) {
                        this.loading = true;
                        Swal.fire(this.$t("alerts.noinvoice"));
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            }
        },
        seeMore(invoice) {
            sessionStorage.setItem("invoice_id", invoice.invoiceId);
            sessionStorage.setItem("client_id", invoice.clientId);
            this.$router.push({name: "InvoiceFull"})
        },
        redirecting() {
            GlobalMethods.isAClient();
        },
    }
}
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
}

.allcards {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    flex-direction: row;
}

.cards {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 250px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    margin: 10px;
    border-radius: 30px;
    height: fit-content;
}

.bottombuttons {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 10px;
}

.titre {
    color: rgb(138, 150, 253);
    font-size: 30px;
}

</style>