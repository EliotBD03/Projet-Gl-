<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="proposal.proposalName"/>
        </div>
        <div class="list">
            <p> Information :</p>
            <p> Provider :  {{ proposal.nameProvider }} </p>
            <p> Type of Energy :  {{ proposal.typeOfEnergy  }}</p>
            <p> Location: </p> 
            <li v-for="item in proposal.location" :key="item">
                {{ item }}
            </li>
            <p> Basic price : {{ proposal.basicPrice }}</p>
            <p> price depends on the day : {{ proposal.variableDayPrice }}</p>
            <p> price depends on the night : {{ proposal.variableNightPrice }}</p>
            <p> Variable or fixed rate : {{ this.proposal.fixedRate }}</p>
            <p> off-peak hours : {{ proposal.startOffPeakHours }} - {{ proposal.endOfPeakHours }}</p>
            <p> Bi-hourly or single-hourly counter : {{ this.proposal.isSingleHour }}</p>
        </div>
        <div class="input">
            <form id="input" method="submit" @submit.prevent="submit">
                <InputMain :text="$t('address')" v-model="address"/>
                <InputMain :text="$t('EAN code')" v-model="ean"/>
                <GoButton :text="submitText" @click="submit()" :colore="'green'"/>
            </form>
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
                    console.log(this.proposal.isFixedRate);
                    let actualLoc = [];
                    const references = ["Brussels-Capital", "Flanders", "Wallonia"];
                    for(let i = 0; i <= this.proposal.location.length; i++)
                    {
                        if(this.proposal.location.substring(i,i + 1) == "1")
                        {
                            actualLoc.push(references[i]);
                        }
                    }
                    this.proposal.location = actualLoc;
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
                    body: JSON.stringify({ name_proposal: this.proposalName, id_provider: this.providerId, ean: this.ean, address: this.address})
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
                return this.ean.length == 18;
            },
            submit()
            {
                if(this.VerifyEan())
                    this.makeContractRequest();
                else
                    Swal.fire("please put a correct EAN code (18 digits)")
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
