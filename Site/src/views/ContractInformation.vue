<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="proposal.proposalName"/>
        </div>
        <div class="list">
            <p> Information :</p>
            <p> Provider : {{ proposal.nameProvider }}</p>
            <p> Type of Energy : {{ typeEnergy }}</p>
            <p> Location : proposal.location</p>
            <p> Basic price : proposal.basicPrice</p>
            <p> price depends on the day : proposal.variableDayPrice</p>
            <p> price depends on the night : proposal.variableNightPrice</p>
            <p> Variable or fixed rate : proposal.isFixedRate</p>
            <p> off-peak hours : {{ proposal.startOffPeakHours }} - {{ proposal.endOfPeakHours }}</p>
            <p> bi-hourly or single-hourly counter : proposal.isSingleHourCounter</p>
        </div>
        <div class="input">
            <p>
                <InputMain :text="$t('address')" v-model="address"/>
            </p>
            <p>
                <InputMain :text="$t('EAN code')" v-model="ean"/>
            </p>
            <p>
                <GoButton :text="submitText" @click="Submit()" :colore="'green'"/>
            </p>
        </div>
        <div class="backbutton" @click.prevent.left="$router.push('/newcontracts')">
            <GoButton text="Back" :colore="'#B1B9FC'"/>
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
                submitText: "Submit",
                address: '',
                ean: '',
                providerId: sessionStorage.getItem("providerId"),
                proposalName: sessionStorage.getItem("proposalName"),
                proposal: []
            }
        },
        mounted()
        {
            if(this.$cookies.get("lang"))
                this.$i18n.locale = this.$cookies.get("lang");
            else
                this.$cookies.set("lang", this.$i18n.locale)
        },
        async created()
        {
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
                    if(response.status == 401)
                        throw new Error("Token");
                    else
                    {
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                }
                else
                {
                    const data = await response.json();
                    this.proposal = data.proposal;
                }
            }
            catch(error)
            {
                if(error.message === "Token")
                {
                    this.$cookies.remove("token");
                    this.$cookies.remove("role");
                    Swal.fire('Your connection has expired');
                    this.$router.push("/")
                }
                else
                    GlobalMethods.errorApi(error.message);
            }
        },
        methods:
        {
            makeContractRequest()
            {
                const requestOptions = 
                {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({ nameProposal: this.proposalName, idProvider: this.providerId, ean: this.ean, address: this.address})
                };
                fetch("https://babawallet.alwaysdata.net/api/client/proposeContract/", requestOptions)
                    .then(response => 
                    {
                        if(!response.ok)
                        {
                            if(response.status == 401)
                                throw new Error("Token");
                            else
                                return response.json().then(json => Promise.reject(json));
                        }
                        else
                        {
                            Swal.fire({
                                icon: "success",
                                title: 'Good !',
                                text: 'A message has been sent to the provider !'
                            })
                            this.$router.push({name: 'NewContractsPage'});
                        }
                    })
                    .catch(error =>
                    {
                        if(error.message === "Token")
                        {
                            GlobalMethods.errorToken();
                        }
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
                return this.eanCode.length == 18;
            },
            VerifyWallet()
            {
             //TODO
            },
            submit()
            {
                if(this.VerifyEan() && this.VerifyWallet())
                {
                    this.makeContractRequest();
                }
            }
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
    height: 500px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    }
</style>
