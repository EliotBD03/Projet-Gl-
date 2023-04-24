<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.invoice"/>
        </div>
        <div class="container">
            <div class="infos">
                <p><b>{{ $t("invoices.associatedcontract") }}</b> :
                    {{ invoice.contractId }}</p>
                <p><b>{{ $t("invoices.price") }}</b> :
                    {{ invoice.price }} €</p>
                <p><b>{{ $t("invoices.proposal") }}</b> :
                    {{ invoice.proposal }} €</p>
                <p><b>{{ $t("invoices.method") }}</b> :
                    {{ parseMethod() }}</p>
                <p><b>{{ $t("invoices.remaining") }}</b> :
                    {{ invoice.remaining }} €</p>
            </div>
            <div class="infos">
                <p class="titre"><b>{{ $t("invoices.accountinfos")}}</b></p>
                <p><b>{{$t("invoices.name")}}</b> : {{bank.accountName}}</p>
                <p><b>{{$t("invoices.number")}}</b> : {{bank.accountNumber}}</p>
                <p><b>{{$t("invoices.expirationdate")}}</b> : {{bank.expirationDate}}</p>
                <p><b>{{$t("invoices.paymentdate")}}</b> : {{invoice.paymentDate}}</p>
            </div>
            <div class="buttons">
                <div class="changeproposal" @click.prevent.left="changeProposal()">
                    <GoButton text="button.changeproposal" :colore="'red'"/>
                </div>
                <div class="changemethod" @click.prevent.left="changeMethod()">
                    <GoButton text="button.changemethod" :colore="'red'"/>
                </div>
                <div class="viewinfos" @click.prevent.left="$router.push({name: 'ChangeAccount'})">
                    <GoButton text="button.changeaccountinformations" :colore="'red'"/>
                </div>
            </div>
        </div>
        <div class="bottombuttons">
            <div class="homebutton" @click.prevent.left="redirecting()">
                <GoButton text="header.home" :colore="'#B1B9FC'"/>
            </div>
            <div class="pay" @click.prevent.left="pay()" v-if="testPay()">
                <GoButton text="button.pay" :colore="'#34c98e'"/>
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
import Swal from "sweetalert2";
import Promise from 'bluebird';

export default {
    components: {
        MainHeader,
        GoButton
    },
    data() {
        return {
            invoice_id: sessionStorage.getItem("invoice_id"),
            client_id: sessionStorage.getItem("client_id"),
            invoice: [],
            bank: []
        }
    },
    created() {
        this.getInvoice();
        this.getBank();
        GlobalMethods.getCurrentLanguage();
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
        },
        async getBank() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            };
            try {
                const response = await fetch("https://babawallet.alwaysdata.net/api/client/invoices/account/" + this.client_id, requestOptions);
                if(!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                } else {
                    const data = await response.json()
                    this.bank = data.bank;
                }
            } catch (error) {
                if (error.error === "error.unauthorizedAccess") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        redirecting() {
            sessionStorage.removeItem("invoice_id");
            sessionStorage.removeItem("client_id");
            this.$router.push({name: "HomeClient"});
        },
        back() {
            sessionStorage.removeItem("invoice_id");
            sessionStorage.removeItem("client_id");
            this.$router.push({name: "Invoices"});
        },
        parseMethod() {
            if (this.invoice.paymentMethod === "0") {
                return this.$t("invoices.auto");
            } else {
                return this.$t("invoices.manual");
            }
        },
        calculateProposal() {
            return Math.floor(this.invoice.price / 12);
        },
        testPay() {
            return this.invoice.paymentMethod !== "0";
        },
        changeMethod(){
            Swal.fire({
                title: 'Choisissez votre méthode de paiement',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: this.$t("invoices.auto"),
                cancelButtonText: this.$t("invoices.manual"),
                reverseButtons: true
            }).then((result) => {
                if (result.isConfirmed) {
                    this.putMethod("0");
                } else if (result.dismiss === Swal.DismissReason.cancel) {
                    this.putMethod("1");
                }
            })
        },
        putMethod(paymentMethod) {
            const requestOptions = {
                method: "PUT",
                headers: {'Authorization': this.$cookies.get("token")},
                body: JSON.stringify({
                    payment_method: paymentMethod
                })
            };
            try {
                fetch("https://babawallet.alwaysdata.net/api/client/invoices/" + this.client_id, requestOptions)
                    .then(response => {
                        if(!response.ok) {
                            throw new Error(response.error);
                        } else {
                            Swal.fire({
                                icon: 'success',
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.methodchanged"),
                            })
                        }
                    })
            } catch (error) {
                if (error.error === "error.unauthorizedAccess") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            } finally {
                this.refresh();
            }
        },
        async refresh() {
            await Promise.delay(2000)
            window.location.reload();
        },
        changeProposal(){
            sessionStorage.setItem("proposal", this.invoice.proposal);
            this.$router.push({name: "ChangeProposal"});
        },
        pay() {
            sessionStorage.setItem("invoice_id", this.invoice_id);
            sessionStorage.setItem("price", this.invoice.proposal);
            this.$router.push({name: "Payment"});
        }
    }
}
</script>

<style scoped>
.main{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    height: 100vh;
}

.infos{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 600px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177,185,252,1);
    height: fit-content;
    margin-top: 10px;
    margin-bottom: 10px;
}

.buttons {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.container {
    display: flex;
    justify-content: space-evenly;
    flex-direction: row;
    align-items: center;
}

.bottombuttons{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 95%;
    padding: 0 50px;
    margin-top: 50px;
}

.titre{
    color: rgb(138, 150,253);
    font-size: 20px;
}
</style>