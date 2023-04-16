<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="contract.proposalName" />
        </div>
        <div class="informations">
            <div class="toptext">
                <p>
                    <b>{{ $t("proposal.typeofenergy") }}</b> : {{ convertEnergy()  }}
                </p>
            </div>
            <p>
                <b>{{ $t("proposal.location") }}</b> : {{ convertLocation(this.location) }}
            </p>
            <p>
                <b>{{ $t("proposal.priceperday") }}</b> : {{ contract.variableDayPrice }} €
            </p>
            <p>
                <b>{{ $t("proposal.pricepernight") }}</b> : {{ contract.variableNightPrice }} €
            </p>
            <p v-if="contract.fixedRate">
                <b>{{ $t("proposal.rate") }}</b> : {{ $t("proposal.fixed") }}
            </p>
            <p v-else>
                <b>{{ $t("proposal.rate") }}</b> : {{ $t("proposal.variable") }}
            </p>
            <div class="bottomtext">
                <p v-if="checkCounter(contract.variableNightPrice)">
                    <b>{{ $t("proposal.counter") }}</b> : {{ $t("proposal.bihourly") }}
                </p>
                <p v-else>
                    <b>{{ $t("proposal.counter") }}</b> : {{ $t("proposal.monohourly") }}
                </p>
                <div v-if="checkCounter(contract.variableNightPrice)">
                    <p>
                        <b>{{ $t("proposal.startofpeakhours") }}</b> : {{ contract.startOfPeakHours }}
                    </p>
                    <p>
                        <b>{{ $t("proposal.endofpeakhours") }}</b> : {{ contract.endOfPeakHours }}
                    </p>
                </div>
                <p>
                    <b>{{ $t("proposal.duration") }}</b> : {{ duration }}
                </p>
            </div>
        </div>
        <div class="bottombuttons">
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'darkblue'"/>
            </div>
            <div class="changebutton" @click.prevent.left="modifyContract()" v-if="canChange()">
                <GoButton text="button.change" :colore="'#34c98e'"/>
            </div>
            <div class="closebutton" @click.prevent.left="deleteProposal()">
                <GoButton text="button.closeproposal" :colore="'red'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from "sweetalert2";
import GoButton from "@/components/GoButton.vue";

export default {
    components: {
        MainHeader,
        GoButton
    },
    data() {
        return {
            name_proposal: sessionStorage.getItem('name_proposal'),
            contract: [],
            location: '',
            duration: 0,
            display: false,
        }},
    created() {
        this.getProposal();
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        canChange() {
            return !this.contract.fixedRate || this.contract.variableNightPrice !== 0;
        },
        async getProposal() {
            const requestOptions = {
                method: 'GET',
                headers: {'Authorization' : this.$cookies.get('token')}
            };
            try {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/provider/proposals/${this.name_proposal}`,requestOptions);
                if (!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                }
                else {
                    const data = await response.json();
                    this.contract = data.proposal;
                    this.location = data.proposal.location;
                    this.priceperday = data.proposal.variableDayPrice;
                    this.pricepernight = data.proposal.variableNightPrice;
                    this.duration = data.proposal.duration/720;

                    if(this.pricepernight > 0) {
                        this.display = true;
                    }
                }
            }
            catch(error) {
                if(error.error === "error.unauthorizedAccess")
                    GlobalMethods.errorToken();
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        back() {
            sessionStorage.removeItem('name_proposal');
            this.$router.push({name: 'ContractsSupplier'});
        },
        modifyContract(){
            this.$router.push({name: 'ModifyProposal'});
        },
        convertEnergy() {
            if (this.contract.typeOfEnergy === "gas") {
                return this.$t("proposal.gas");
            }
            else if (this.contract.typeOfEnergy === "electricity") {
                return this.$t("proposal.electricity");
            }
            else {
                return this.$t("proposal.water");
            }
        },
        convertLocation: function(location) {
            const result = [];

            if (location >= 100) {
                result.push(this.$t("proposal.wallonia"));
                location -= 100;
            }

            if (location >= 10) {
                result.push(this.$t("proposal.flanders"));
                location -= 10;
            }

            if (location >= 1) {
                result.push(this.$t("proposal.brussels"));
            }

            return result.join(' - ');
        },
        deleteProposal(){
            const requestOptions = {
                method: 'DELETE',
                headers: {'Authorization' : this.$cookies.get('token')}
            };
            fetch(`https://babawallet.alwaysdata.net/api/provider/proposals/${this.name_proposal}`,requestOptions)
                .then(response => {
                    if(!response.ok){
                        return response.json().then(json => Promise.reject(json));
                    }
                    else{
                        Swal.fire({
                            icon: 'success',
                            title: this.$t("alerts.good"),
                            text: this.$t("alerts.deletedproposal")
                        })
                        this.$router.push({name: 'ContractsSupplier'});
                    }
                })
                .catch(error => {
                    if(error.error === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else {
                        GlobalMethods.errorApi(error.error);
                    }
                });
        },
        checkCounter(value) {
            return value !== 0;
        }
    }
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 100vh;
}

.informations {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 600px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    height: fit-content;
}

.bottombuttons {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 95%;
    padding: 0 50px;
    margin-top: 50px;
}

.toptext {
    margin-top: 50px;
}

.bottomtext {
    margin-bottom: 50px;
}

</style>