<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="proposal.proposalName"/>
        </div>
        <div class="list">
            <p><b> {{ $t("account.provider") }} :</b>  {{ proposal.nameProvider }} </p>
            <p> <b>{{ $t("proposal.typeofenergy") }} :</b>  {{ proposal.typeOfEnergy  }}</p>
            <p> <b>{{ $t("proposal.location") }} :</b> </p>
            <p>{{ convertLocation(proposal.location) }}</p>
            <p> <b>{{ $t("proposal.priceperday") }} :</b> {{ proposal.variableDayPrice }}</p>
            <p> <b>{{ $t("proposal.pricepernight") }} :</b> {{ proposal.variableNightPrice }}</p>
            <p v-if="proposal.isFixedRate"><b>{{ $t("proposal.rate") }} :</b>{{ $t("proposal.fixed") }}</p>
            <p v-else><b>{{ $t("proposal.rate") }} :</b>{{ $t("proposal.variable") }}</p>
            <p> <b>{{ $t("proposal.peakhours") }} :</b> {{ proposal.startOffPeakHours }} - {{ proposal.endOfPeakHours }}</p>
            <div class="input">
                <form id="input" method="submit" @submit.prevent="submit">
                    <InputMain :text="$t('walletform.address')" v-model="address"/>
                    <InputMain :text="$t('client.eancode')" v-model="ean"/>
                    <GoButton text="button.submit" @click="submit()" :colore="'green'"/>
                </form>
            </div>
        </div>
        <div class="backbutton" @click.prevent.left="$router.push('/newcontracts')">
            <GoButton text="button.back" :colore="'#B1B9FC'"/>
        </div>
    </div>
</template>

<script>

import MainHeader from '@/components/MainHeader.vue';
import GoButton from '@/components/GoButton.vue';
import  Swal  from 'sweetalert2/dist/sweetalert2';
import GlobalMethods from '@/components/GlobalMethods.vue';
import InputMain from '@/components/InputMain.vue';

export default
{
    components:
        {
            GoButton,
            MainHeader,
            InputMain
        },
    data()
    {
        return{
            address: '',
            ean: '',
            providerId: sessionStorage.getItem("providerId"),
            proposalName: sessionStorage.getItem("proposalName"),
            proposal: []
        }
    },
    created() {
        this.getProposal();
        GlobalMethods.getCurrentLanguage();
    },
    methods:
        {
            async getProposal() {
                const requestOptions =
                    {
                        method: "GET",
                        headers: {'Authorization': this.$cookies.get("token")}
                    };
                try
                {
                    const response = await fetch(`https://babawallet.alwaysdata.net/api/client/proposals/${this.providerId}/${this.proposalName}`, requestOptions);
                    if(!response.ok)
                    {
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                    else
                    {
                        const data = await response.json();
                        this.proposal = data.proposal;
                    }
                }
                catch(error)
                {
                    if(error.error === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else
                        GlobalMethods.errorApi(error.message);
                }
            },
            makeContractRequest()
            {
                const requestOptions =
                    {
                        method: "POST",
                        headers: {'Authorization': this.$cookies.get("token")},
                        body: JSON.stringify({ name_proposal: this.proposalName, id_provider: this.providerId, ean: this.ean, address: this.address})
                    };
                fetch("https://babawallet.alwaysdata.net/api/client/proposeContract/", requestOptions)
                    .then(response =>
                    {
                        if(!response.ok)
                        {
                            return response.json().then(json => Promise.reject(json));
                        }
                        else
                        {
                            Swal.fire({
                                icon: "success",
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.messagesentprovider")
                            })
                            this.$router.push({name: 'NewContracts'});
                        }
                    })
                    .catch(error =>
                    {
                        if(error.error === "error.unauthorizedAccess")
                            GlobalMethods.errorToken();
                        else
                            GlobalMethods.errorApi(error.error);
                    });
            },
            back()
            {
                sessionStorage.clear();
                this.$router.push({name: "NewContractsPage"})
            },
            VerifyEan: function()
            {
                return this.ean.length == 18 && this.ean.substring(0,4) == "5414";
            },
            submit()
            {
                if(this.VerifyEan())
                    this.makeContractRequest();
                else
                    Swal.fire(this.$t("alerts.wrongean"))
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
        }
}
</script>

<style>
.main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.list{
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 500px;
    height: 700px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}
</style>
